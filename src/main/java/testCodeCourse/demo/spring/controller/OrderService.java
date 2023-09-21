package testCodeCourse.demo.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import testCodeCourse.demo.spring.domain.order.Order;
import testCodeCourse.demo.spring.domain.order.request.OrderCreateRequest;
import testCodeCourse.demo.spring.domain.order.response.OrderResponse;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderCreateRequest request,LocalDateTime registerDateTime) {

        List<String> productNumbers = request.getProductNumbers();

        List<Product> products = productRepository.findAllByProductAllProductNumberIn(productNumbers);
        Order order = Order.create(products, registerDateTime);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.of(savedOrder);
    }
}
