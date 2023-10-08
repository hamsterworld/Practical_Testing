package testCodeCourse.demo.spring.domain.stock;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String productNumber;

    private int quantity;

    @Builder
    private Stock(String productNumber, int quantity) {
        this.productNumber = productNumber;
        this.quantity = quantity;
    }


    public static Stock create(String productNumber, int quantity) {
        return Stock.builder()
                .productNumber(productNumber)
                .quantity(quantity)
                .build();
    }

    public boolean isQuantityLessThan(int quantity) {
        return this.quantity < quantity;
    }

    public void deductQuantity(int quantity) {
        // 보면은 serviceLayer 에도 validation 이 있다.
        // 왜 2개를 중복해서 넣어주냐 어차피 안걸릴텐데?
        // Stock 은 serviceLayer 를 전혀모른다.
        // 그래서 요 method 에서 수량을 차감을 올바르게 해주기위해서 보장해주어야한다.
        // 그래서 보면, 같은상황이지만 메시지를 다르게적었다.
        // 또한 메시지를 다르게주고싶을수도 있기때문이다.
        if(isQuantityLessThan(quantity)){
            throw new IllegalArgumentException("차감할 재고 수량이 없습니다.");
        }
        this.quantity -= quantity;
    }
}
