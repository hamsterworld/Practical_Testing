package testCodeCourse.demo.spring.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import testCodeCourse.demo.spring.domain.order.request.OrderCreateRequest;
import testCodeCourse.demo.spring.domain.order.response.OrderResponse;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static testCodeCourse.demo.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1,product2,product3));
        OrderCreateRequest request = OrderCreateRequest.builder()
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

    private Product createProduct(ProductType type,String productNumber,int price){
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .name("메뉴 이름")
                .build();
    }

}