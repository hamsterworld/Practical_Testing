package testCodeCourse.demo.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import testCodeCourse.demo.spring.domain.order.Order;
import testCodeCourse.demo.spring.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {


    @Query("select o from Order o where o.registeredDateTime >= :startDateTime and o.registeredDateTime < :endDateTime and o.orderStatus = :orderStatus")
    List<Order> findOrdersBy(LocalDateTime startDateTime, LocalDateTime endDateTime, OrderStatus orderStatus);
}
