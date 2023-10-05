package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import testCodeCourse.demo.IntegrationTestSupport;
import testCodeCourse.demo.spring.controller.product.request.ProductCreateRequest;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductResponse;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.*;
import static testCodeCourse.demo.spring.domain.product.ProductSellingStatus.*;
import static testCodeCourse.demo.spring.domain.product.ProductType.HANDMADE;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // 준비절에서 같은 데이터를 만드는 경우가 많을때가 있다.
    // 그래서 계속 동일한 entity 들 즉, fixture 들을 만들때가 많다.
    // 그러나 이런것들은 공유자원의 문제가 생기게된다.
    // 즉,테스트간의 연관성이 생기게된다. 결합도가 상승한다.
    // fixture 를 수정하는순간 모든 테스트에 영향을 미칠수있다.
    // 지양하는것이 좋다.
    // 그럼 도대체 언제쓰냐?

    // 각 테스트 입장에서 봤을때 :
    // 아예 몰라도 테스트 내용을 이해하는 데에 문제가 없는가?
    // 수정해도 모든 테스트에 영향을 주지 않는가?
    // 예를들어서, ProductService 를 테스트하는데에는 이 Entity 를 전혀몰라도된다.
    // 그러나 Product 를 생성하는데는 꼭필요하다.
    // 이런경우에는 위 2가지 조건을 충족한다.

    // 첫번째로, fixture 들은 항상 준비절에 있으면 좋다. (beforeAll,beforeEach 는 그래서 위 2조건을 만족하는 fixture 만 사용하자.)
    // 준비절은 길어질것이다. 그러나 문서로써의 테스트를 생각하면서, 준비절을 구성하면 좋을것같다.

    // 두번째로, 준비절이 너무길다. 즉, 준비해야될 데이터가 너무 많다.
    // 미리구성하고싶어서 data.sql 로 이프로젝트를 실행시킬때
    // 이런데이터를 넣어줘 내가 매번 테스트용 데이터를 하기 귀찮으니까 넣어줘 해서 구문을 작성해서 했었는데
    // 이것을 테스트에서도 활용할수있다. 똑같이 java resource package 를 만들어서 data.sql 을 만들어서할수있다.
    // 예를들어서, 통합테스트겠지? 그래서 테스트를 띄우는 것을 수행할때마다 이 insert 를 실행시켜줘 할수있다.
    // 여기서 넣은것으로 테스트할래 할수있다. 그러면 사실상 준비절은 data.sql 이 준비해주는것이다.
    // 라고 구성을 할 수도 있는데 이렇게 하지말아라.
    // 파편화가 일어난다. 하나의 문서, 즉 하나의 테스트를 볼때 어? 왜 준비절도 없고 test_fixture 가없지?
    // 알고보니 data.sql 에 있었던거임 파편화되서 그래서 내가 뭘 테스트할지 정말어려워진다.
    // 그다음 프로젝트가 커질수록 SQL 이 엄청 많아질것이다. 다루는 테이블도 많아질것이고등등
    // 새로운필드가 생긴다던지 스키마가 변경된다던지 등등 그러면 다 변경해줘야된다.
    // 그러면 우리는 data.sql 까지도 따로 관리해줘야되는 관리포인트가 여러개가 된다. 굉장히 추천하지않는 방법.

    // 세번째로는 빌더가 길어지니까 매서드로 따로 빼서 사용했었다.
    // 그래서 이럴때도 테스트클래스내에서 필요한것들만 남겨놓으면 좋다.
    // 만약에 이번테스트에서 name 필드가 중요하지않다면
    // 파라미터에서는 name 을 받지않고 그냥 "아메리카노"이렇게박아놓고 사용할수도있다.
    // 테스트에 필요한 필드만 파라미터로 받는 명시가 있으면 좋다.
    // 즉, 테스트 클래스마다 각자 필요한 파라미터만 뽑아서 필요한 필드만 사용하자는게 좋다고생각함
    // 물론 테스트클래스마다 이거 만드는게 귀찮은데, 코틀린을 사용하면 어느정도 해소가된다.

    @BeforeAll
    static void beforeAll(){
        // before class
    }

    @BeforeEach
    void setUp(){
        // before method
    }

    @AfterEach
    void tearDown(){
        productRepository.deleteAllInBatch();
    }

    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최곤 상품의 상품번호에서 1 증가한 값이다.")
    @Test
    void createProduct() {

        Product product1 = createProduct("001", HANDMADE,SELLING,"아메리카노",4000);
        productRepository.saveAll(List.of(product1));

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        ProductResponse productResponse = productService.createProduct(request);

        assertThat(productResponse)
                .extracting("productNumber","type","sellingStatus","name","price")
                .contains("002",HANDMADE,SELLING,"카푸치노",5000);

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2)
                .extracting("productNumber","type","sellingStatus","name","price")
                .containsExactlyInAnyOrder(
                        tuple("001",HANDMADE,SELLING,"아메리카노",4000),
                        tuple("002",HANDMADE,SELLING,"카푸치노",5000)
                );

    }

    @DisplayName("상품이 하나도 없는경우 신규 상품을 등록하면 상품번호는 001 이다.")
    @Test
    void createProductWhenProductIsEmpty() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        ProductResponse productResponse = productService.createProduct(request);

        assertThat(productResponse)
                .extracting("productNumber","type","sellingStatus","name","price")
                .contains("001",HANDMADE,SELLING,"카푸치노",5000);
    }


    private static Product createProduct(String productNumber,
                                         ProductType productType,
                                         ProductSellingStatus sellingStatus,
                                         String productName,
                                         int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .sellingStatus(sellingStatus)
                .name(productName)
                .price(price)
                .build();
    }
}