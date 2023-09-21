package testCodeCourse.demo.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static testCodeCourse.demo.spring.domain.product.ProductType.HANDMADE;

class OrderTest {

    @Test
    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
    void calculateTotalPrice() {

        List<Product> products = List.of(
                createProduct("001", 1000),
                createProduct("002", 2000)
        );
        LocalDateTime registerDateTime = LocalDateTime.now();
        Order order = Order.create(products, registerDateTime);

        assertThat(order.getTotalPrice()).isEqualTo(3000);

    }

    private Product createProduct(String productNumber, int price){
        return Product.builder()
                .type(HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .name("메뉴 이름")
                .build();
    }
}