package testCodeCourse.demo.spring.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import testCodeCourse.demo.spring.ApiResponse;
import testCodeCourse.demo.spring.controller.order.request.OrderCreateRequest;
import testCodeCourse.demo.spring.domain.order.response.OrderResponse;
import testCodeCourse.demo.spring.service.OrderService;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    /**
     * 지금 현재 OrderCreateRequest 를 service 에 그대로 넘기고있다.
     * 다시말하면 OrderCreateRequest 를 service layer 까지 내려서 사용하고있다.
     * 이는 좋은 상황은 아니다. package 로도 구분되어있다.
     * 지금 이것은 OOP 적으로도 의존성이 생겨버린것이다.
     * Service 가 상위계층을 알아버린 상황이다.
     * 즉, 의존관계가 생겨버린것
     * 제일 좋은것은 하위 layer 가 상위 layer 를 몰라야한다.
     * 상위 layer 는 하위 layer 를 당연히 알아야하지만 그반대는 아니다.
     *
     * 그래서 아래처럼 ServiceRequest 라는 Dto 로 변경해줬을때
     * service Layer 에서는 service 관련 object 만 사용하고있다.
     * controller 에 대한 정보를 아무것도 모르게 된다.
     * 이제 controller 에서만 field 에대한 validation 책임을 가져갈수있게된다.
     *
     * 위작업은 귀찮다.
     * 그러나 서비스가 커지면 커질수록 부담이 될것이다.
     * 지금은 키오스크에서만 주문을 받지만, 만약에 포스기주문,웹주문,등등 주문받는 채널이 여러개가 될수있다.
     * 그때마다 나는 같은 service(OrderService Object)를 사용하고싶은데, 해당 서비스가 만약에
     * 전처럼 키오스크 Controller Dto 를 사용하고있다면 이 Dto 를 사용하고있다면?
     * 해당 Dto 를 다른 controller API 들도 전부다 알아야한다.
     * Service 가 얘를 사용하고있기때문이다. 이것에 맞춰서 넣어줘야한다.
     * 그래서 이렇게 분리를 해줘서
     * Service Layer 가 Controller Layer 에 변경에 영향을 받지않는게 제일베스트이다.
     */
    @PostMapping("/api/v1/orders/new")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request){
        return ApiResponse.ok(orderService.createOrder(request.toServiceRequest(), LocalDateTime.now()));
    }



}
