package testCodeCourse.demo.spring;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import testCodeCourse.demo.spring.domain.product.ProductResponse;

import static org.springframework.http.HttpStatus.OK;

// 이런식으로 return format 을 정해주는것이 좋다.
@Getter
public class ApiResponse<T> {

    private HttpStatus status;
    private int code;
    private String message;
    private T data;

    @Builder
    private ApiResponse(HttpStatus status, String message, T data) {
        this.status = status;
        this.code = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus status,String message,T data) {
        return new ApiResponse<>(status, message,data);
    }

    public static <T> ApiResponse<T> of(HttpStatus status, T data) {
        return of(status, status.name(),data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(OK,OK.name(),data);
    }
}
