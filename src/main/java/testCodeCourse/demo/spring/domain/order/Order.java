package testCodeCourse.demo.spring.domain.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.BaseEntity;
import testCodeCourse.demo.spring.domain.product.Product;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@Table(name = "orders")
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated
    private OrderStatus orderStatus;

    private int totalPrice;

    private LocalDateTime registerDateTime;


    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Builder
    private Order(List<Product> products,OrderStatus orderStatus,LocalDateTime registerDateTime){
        this.orderStatus = orderStatus;
        this.totalPrice = calculateTotalPrice(products);
        this.registerDateTime = registerDateTime;
        this.orderProducts = products.stream()
                .map(product -> new OrderProduct(this,product))
                .collect(Collectors.toList());
    }


    public static Order create(List<Product> products,LocalDateTime registerDateTime) {
        return Order.builder()
                .orderStatus(OrderStatus.INIT)
                .products(products)
                .registerDateTime(registerDateTime)
                .build();
    }

    private int calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToInt(Product::getPrice)
                .sum();
    }
}
