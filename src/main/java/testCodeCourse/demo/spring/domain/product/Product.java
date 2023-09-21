package testCodeCourse.demo.spring.domain.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String productNumber;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    private ProductSellingStatus sellingStatus;
    private String name;

    private int price;

    @Builder
    private Product(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.productNumber = productNumber;
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }
}
