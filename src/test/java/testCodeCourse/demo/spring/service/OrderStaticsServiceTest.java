package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import testCodeCourse.demo.spring.client.MailSendClient;
import testCodeCourse.demo.spring.domain.history.MailSendHistory;
import testCodeCourse.demo.spring.domain.order.Order;
import testCodeCourse.demo.spring.domain.order.OrderStatus;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.repository.MailSendHistoryRepository;
import testCodeCourse.demo.spring.repository.OrderProductRepository;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static testCodeCourse.demo.spring.domain.product.ProductType.*;

@SpringBootTest
class OrderStaticsServiceTest {

    @Autowired
    private OrderStaticsService orderStaticsService;

    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @MockBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown(){
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송합니다.")
    void sendOrderStatisticsMail() {
        // 준비
        LocalDateTime now = LocalDateTime.of(2023, 3, 5, 0, 0);

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "003", 3000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        Order order1 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,4,23,59), products);
        Order order2 = createPaymentCompletedOrder(now, products);
        Order order3 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,5,23,59), products);
        Order order4 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,6,0,0), products);

        when(mailSendClient.sendEmail(anyString(),anyString(),anyString(),anyString()))
                .thenReturn(true);

        // 실행
        boolean result = orderStaticsService.sendOrderStatisticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        // 검증
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원입니다.");
    }

    private Order createPaymentCompletedOrder(LocalDateTime now, List<Product> products) {
        Order order1 = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registerDateTime(now)
                .build();
        return orderRepository.save(order1);
    }

    private Product createProduct(ProductType type, String productNumber, int price){
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .name("메뉴 이름")
                .build();
    }

}