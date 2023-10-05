package testCodeCourse.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import testCodeCourse.demo.spring.controller.order.OrderController;
import testCodeCourse.demo.spring.controller.product.ProductController;
import testCodeCourse.demo.spring.service.OrderService;
import testCodeCourse.demo.spring.service.ProductService;

@WebMvcTest(controllers = {
        ProductController.class,
        OrderController.class
}) // Controller 만 올릴수있는 좀더 가벼운 테스트
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected ProductService productService;

    @Autowired
    protected OrderService orderService;


}
