package testCodeCourse.demo.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import testCodeCourse.demo.IntegrationTestSupport;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static testCodeCourse.demo.spring.domain.product.ProductSellingStatus.*;
import static testCodeCourse.demo.spring.domain.product.ProductType.HANDMADE;

//@ActiveProfiles("test")
//@SpringBootTest
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    void findAllByProductSellingStatusIn() {
        Product product1 = createProduct("001", HANDMADE,SELLING,"아메리카노",4000);
        Product product2 = createProduct("002", HANDMADE,HOLD,"카페라떼",4500);
        Product product3 = createProduct("003", HANDMADE,STOP_SELLING,"팥빙수",7000);

        productRepository.saveAll(List.of(product1,product2,product3));


        // 실행
        List<Product> products = productRepository.findAllByProductSellingStatusIn(List.of(SELLING, HOLD));


        // 검증
        assertThat(products).hasSize(2)
                .extracting("ProductName","name","sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001","아메리카노",SELLING),
                        tuple("002","카페라떼",HOLD)
                );
    }

    @DisplayName("상품번호리스트로 상품들을 조회한다.")
    @Test
    void findAllByProductAllProductNumberIn() {
        Product product1 = createProduct("001", HANDMADE,SELLING,"아메리카노",4000);
        Product product2 = createProduct("002", HANDMADE,HOLD,"카페라떼",4500);
        Product product3 = createProduct("003", HANDMADE,STOP_SELLING,"팥빙수",7000);

        productRepository.saveAll(List.of(product1,product2,product3));


        // 실행
        List<Product> products = productRepository.findAllByProductAllProductNumberIn(List.of("001", "002"));


        // 검증
        assertThat(products).hasSize(2)
                .extracting("ProductName","name","sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001","아메리카노",SELLING),
                        tuple("002","카페라떼",HOLD)
                );
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올 때,상품이 하나도 없는 경우에는 null 을 반환한다.")
    void findLatesProductNumberWhenProductIsEmpty() {


        // 실행
        String latesProductNumber = productRepository.findLatesProductNumber();


        // 검증
        assertThat(latesProductNumber).isNull();
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    void findLatesProduct() {

        String targetProductNumber = "003";

        Product product1 = createProduct("001", HANDMADE,SELLING,"아메리카노",4000);
        Product product2 = createProduct("002", HANDMADE,HOLD,"카페라떼",4500);
        Product product3 = createProduct(targetProductNumber, HANDMADE,STOP_SELLING,"팥빙수",7000);
        productRepository.saveAll(List.of(product1,product2,product3));


        // 실행
        String latesProductNumber = productRepository.findLatesProductNumber();


        // 검증
        assertThat(latesProductNumber).isEqualTo(targetProductNumber);
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