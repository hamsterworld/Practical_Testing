package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import testCodeCourse.demo.IntegrationTestSupport;
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

class OrderStaticsServiceTest extends IntegrationTestSupport {

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

    // 얘가 mockBean 으로 띄우기때문에 환경이 달라져서 해당 클래스는 spring 을 새로올릴것이다.
    // 이런경우 2가지선택이있다
    // 아래 mockBean 을 상위클래스로 올리는것.
    // 이런경우는 다른테스트클래스도 mockBean 으로 들어갈거다.
    // 보통은 mockBean 클래스는 외부환경을 처리하기위한 애들이 많다.
    // 두번째방법은, mockBean 으로 들어가는 새로운환경을 만드는것
    // 그냥 즉, 환경을 2개로 만드는것이다.
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