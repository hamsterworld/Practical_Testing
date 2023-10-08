package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import testCodeCourse.demo.IntegrationTestSupport;
import testCodeCourse.demo.spring.domain.order.response.OrderResponse;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.domain.stock.Stock;
import testCodeCourse.demo.spring.repository.OrderProductRepository;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;
import testCodeCourse.demo.spring.repository.StockRepository;
import testCodeCourse.demo.spring.service.request.OrderCreateServiceRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static testCodeCourse.demo.spring.domain.product.ProductType.*;

// @Transactional // 이렇게하면 문제가있다. 일단 여기서는 수동삭제를 다룬다. Business Layer 테스트 (3) 47:00 ~ 48:00 다시
// 참고 save 에 @Transactional 이 걸려있다.
//
class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderService orderService;

    // business Layer test(3)강의중 여기다가 @Transactional 을 걸어주면 rollback 이되니까.
    // 사실아래도 필요없다 굉장히 편하다.
    // 근데 방금실수에서처럼 @Service 에도 @Transactional 걸려있는것처럼 보인다.
    // 그래서 만약에 걸려있지않은채로 @Service 가 배포가되었다던가하면 큰일난다.
    // 물론편해서좋지만 이런 부작용도 있을것을 대비해야한다.

    // 그럼 어왜 insert 는 잘날라갔죠? 안걸어줫을때도?
    // repository 에 @Transactional 이 걸려있는 곳이 있다.
    @AfterEach
    void teatDown(){
        // 순서를 어느정도 신경써주어야한다. 외래키조건때문에.
        // deleteAll() 도 어느정도 신경써줘야하긴함.
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();

        // 둘간의 차이는 무엇일까?
        // 건마다 select 해와서 delete 해오기때문이다. (성능차이가 있을수있다.)
        // deleteAllInBatch() 는 그렇지 않다.
        orderRepository.deleteAll();
        orderRepository.deleteAllInBatch();

        // 결론 deleteAll,deleteAllInBatch,Transaction 이 3가지의 차이를 잘알고 그때그때사용하는것이 좋다.

        stockRepository.deleteAllInBatch();
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1,stock2));


        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001","001","002","003"))
                .build();
        LocalDateTime registerDateTime = LocalDateTime.now();

        OrderResponse orderResponse = orderService.createOrder(request,registerDateTime);

        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime","totlaPrice")
                .contains(LocalDateTime.now(),10000);
        assertThat(orderResponse.getProducts()).hasSize(4)
                .extracting("productNumber","price")
                .containsExactlyInAnyOrder(
                        tuple("001",1000),
                        tuple("001",1000),
                        tuple("002",3000),
                        tuple("003",5000)
                );
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber","quantity")
                .containsExactlyInAnyOrder(
                        tuple("001",0),
                        tuple("002",1)
                );

    }

    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    @Test
    void createOrderWithNoStock() {

        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));

        // 팩토리매서드같은경우도 테스트코드에서는 지양하고
        // 빌더나 생성자로 생성하는것이 좋다.
        // 팩터리매서드코드도 어떻게보면 프로덕션코드에서 의도를 가지고만든코드이다.
        // 생성자로 만들어도되는데 굳이 팩터리매서드를 만들엇다는것은 팩토리매서드내에서 검증을 하고싶다던가.
        // 혹은 내가 필요한 인자만 받아서 생성하고싶다던가. 라는 생성구문이다.
        // 그래서 지양해주자. 그래서 Product 처럼 순수한 builder, 혹은 생성자가 좋다.
        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stock1.deductQuantity(1); // 만약에 3개를 줄이면 여기서 준비절에서 코드가 깨져버린다. 그러면 어 왜깨졌지?
        // todo 맥락을 이해하려는 허들이 생긴다.  (테스트 환경의 독립성을 보장하자) 주문생성실패에서 예외가 떠야한다.
        // 그앞에 준비절 생성하다가 테스트가 깨진것이다. 테스트 주제와 맞지않는 부분에서 깨지게된것.
        // 이테스트가 왜깨진거지? 유추하기가 어려워진다.
        // 결론적으로는 deductQuantity 와같은 API 를 통해서
        // 구성해야지 하는것보다는 최대한 독립적으로 테스트환경을 구성하는게 좋다.
        stockRepository.saveAll(List.of(stock1,stock2));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001","001","002","003"))
                .build();
        LocalDateTime registerDateTime = LocalDateTime.now();

        // 실행,검증
        assertThatThrownBy(() -> orderService.createOrder(request,registerDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다.");
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // 준비
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();
        LocalDateTime registerDateTime = LocalDateTime.now();

        // 실행
        OrderResponse orderResponse = orderService.createOrder(request,registerDateTime);

        // 검증
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime","totlaPrice")
                .contains(LocalDateTime.now(),4000);
        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber","price")
                .containsExactlyInAnyOrder(
                        tuple("001",1000),
                        tuple("002",3000)
                );

    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할수있다.")
    @Test
    void createOrderWithDuplicateProductNumbers(){
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001"))
                .build();
        LocalDateTime registerDateTime = LocalDateTime.now();

        OrderResponse orderResponse = orderService.createOrder(request,registerDateTime);

        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime","totlaPrice")
                .contains(LocalDateTime.now(),2000);
        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber","price")
                .containsExactlyInAnyOrder(
                        tuple("001",1000),
                        tuple("002",1000)
                );
    }

    private Product createProduct(ProductType type,String productNumber,int price){
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .name("메뉴 이름")
                .build();
    }

}