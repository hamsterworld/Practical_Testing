package testCodeCourse.demo.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import testCodeCourse.demo.spring.domain.order.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {
}
