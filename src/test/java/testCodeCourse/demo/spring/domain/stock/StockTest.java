package testCodeCourse.demo.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    // 테스트간 독립성을 보장해야한다.
    // 각각 테스트들의 순서상관없이 성공/실패여부를 판가름할수있어야한다.
    // 아래처럼 stock 공유자원을 생성 해버리고있다.
    // 어떤 테스트순서가 영향을 미칠수있다.
    // 여러테스트가 사용하는 공유자원을 사용하면 안된다.
//    private static final Stock stock = Stock.create("001",1);

    @Test
    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
    void isQuantityLessThan() {
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        boolean result = stock.isQuantityLessThan(quantity);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재고를 주어진 개수만큼 차감할 수 있다.")
    void deductQuantity1() {
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

        stock.deductQuantity(quantity);

        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("재고보다 많은 수의 수량으로 차감 시도하는 경우 예외가 발생한다")
    void deductQuantity2() {
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        assertThatThrownBy(() -> stock.deductQuantity(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");
    }

    // @DynamicTest
    // 공통의 환경에서
    // 1을 감소시켰다가.
    // 다시 1을 감소시킨다.
    // 즉, 시나리오 기반으로 상태를 공유하면서
    // 시나리오대로 테스트를 수행해 나간다.
    // 이런 테스트시나리오대로 자원을 공유하면서
    // 단계단계 나가야하는게있다면 고려해보는것도 괜찮다.
    @DisplayName("재고 차감 시나리오")
    @TestFactory
    Collection<DynamicTest> stockDeductionDynamicTest(){
        // 준비
        Stock stock = Stock.create("001", 1);
        return List.of(
            DynamicTest.dynamicTest("재고를 주어진 개수만큼 차감할수있다.",()->{
                int quantity = 1;
                stock.deductQuantity(quantity);

                assertThat(stock.getQuantity()).isZero();
            }),
            DynamicTest.dynamicTest("재고보다 많은 수의 수량으로 차감 시도하는 경우 예외가 발생한다.",()->{
                int quantity = 1;

                 assertThatThrownBy(() -> stock.deductQuantity(quantity))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessage("차감할 재고 수량이 없습니다.");
            })
        );

    }
}