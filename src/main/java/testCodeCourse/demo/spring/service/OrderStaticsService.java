package testCodeCourse.demo.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import testCodeCourse.demo.spring.domain.order.Order;
import testCodeCourse.demo.spring.domain.order.OrderStatus;
import testCodeCourse.demo.spring.repository.OrderRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStaticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    // 긴작업이 진행되는 대표적으로 네트워크를 타거나 이런애들은
    // 트랜잭션을 안걸어주는것이 좋다. 그래서 어차피 조회할때 트랜잭션이 따로걸리기때문에
    // 이번 service 단에서는 안걸어주는것이 좋다.
    public boolean sendOrderStatisticsMail(LocalDate orderDate, String email){

        // 해당 일자에 결제완료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 할계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일전송
        boolean result = mailService.sendMail(
                "no-reply@cafekiosk.com",
                email,
                String.format("[매출통계] %s",orderDate),
                String.format("총 매출 합계는 %s원입니다.",totalAmount)
        );

        if(!result){
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }
        return true;

    }

}
