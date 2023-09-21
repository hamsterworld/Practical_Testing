package testCodeCourse.demo.spring.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.BaseEntity;
import testCodeCourse.demo.spring.domain.product.Product;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class OrderProduct extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public OrderProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }


}
