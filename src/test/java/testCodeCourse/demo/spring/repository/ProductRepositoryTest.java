package testCodeCourse.demo.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static testCodeCourse.demo.spring.domain.product.ProductSellingStatus.HOLD;
import static testCodeCourse.demo.spring.domain.product.ProductSellingStatus.SELLING;

@ActiveProfiles("test")
@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    void findAllByProductSellingStatusIn() {
        Product product1 = Product.builder()
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        Product product2 = Product.builder()
                .productNumber("002")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("카페라떼")
                .price(4500)
                .build();

        Product product3 = Product.builder()
                .productNumber("003")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("팥빙수")
                .price(7000)
                .build();

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
        Product product1 = Product.builder()
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        Product product2 = Product.builder()
                .productNumber("002")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("카페라떼")
                .price(4500)
                .build();

        Product product3 = Product.builder()
                .productNumber("003")
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("팥빙수")
                .price(7000)
                .build();

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
}