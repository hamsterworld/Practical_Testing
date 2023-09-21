package testCodeCourse.demo.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testCodeCourse.demo.spring.domain.order.Order;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
