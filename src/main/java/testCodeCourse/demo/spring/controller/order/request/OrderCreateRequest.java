package testCodeCourse.demo.spring.controller.order.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.service.request.OrderCreateServiceRequest;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotEmpty(message = "상품 번호 리스트는 필수입니다.")
    private List<String> productNumbers;

    @Builder
    private OrderCreateRequest(List<String> productNumbers) {
        this.productNumbers = productNumbers;
    }


    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .productNumbers(this.productNumbers)
                .build();
    }
}
