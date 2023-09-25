package testCodeCourse.demo.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductTypeTest {

    // 간단한것도 테스트해야되나요? 왜냐면 언제 바뀔지 모르기때문에 대비를 해둬야한다.
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType1() {
        ProductType givenType = ProductType.HANDMADE;

        boolean result = ProductType.containsStockType(givenType);

        assertThat(result).isFalse();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType2() {
        ProductType givenType = ProductType.BAKERY;

        boolean result = ProductType.containsStockType(givenType);

        assertThat(result).isTrue();
    }
}