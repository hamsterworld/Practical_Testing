package testCodeCourse.demo.spring.domain.order.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {

    private List<String> productNumbers;

    @Builder
    private OrderCreateRequest(List<String> productNumbers) {
        this.productNumbers = productNumbers;
    }


}
