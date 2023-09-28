package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import testCodeCourse.demo.spring.controller.order.request.OrderCreateRequest;
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

@ActiveProfiles("test")
@SpringBootTest
// @Transactional // 이렇게하면 문제가있다. 일단 여기서는 수동삭제를 다룬다. Business Layer 테스트 (3) 47:00 ~ 48:00 다시
// 참고 save 에 @Transactional 이 걸려있다.
class OrderServiceTest {

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

    @AfterEach
    void teatDown(){
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        orderProductRepository.deleteAllInBatch();
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

        Stock stock1 = Stock.create("001", 0);
        Stock stock2 = Stock.create("002", 2);
        stock1.deductQuantity(1); // todo
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
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();
        LocalDateTime registerDateTime = LocalDateTime.now();

        OrderResponse orderResponse = orderService.createOrder(request,registerDateTime);

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