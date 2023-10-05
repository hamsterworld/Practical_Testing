package testCodeCourse.demo.spring.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import testCodeCourse.demo.ControllerTestSupport;
import testCodeCourse.demo.spring.controller.product.request.ProductCreateRequest;
import testCodeCourse.demo.spring.domain.product.ProductResponse;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = ProductController.class) // Controller 만 올릴수있는 좀더 가벼운 테스트
class ProductControllerTest extends ControllerTestSupport {

    // MockBean 이란?
    // container 에 mock 으로 만든객체를 넣어준다.
    // 그래서 위가 없으면 ProductController 를 테스트할때 productService 를 주입못해서
    // bean 주입 에러가 난다.


    @Test
    @DisplayName("판매상품을 조회한다.")
    void getSellingProducts() throws Exception {
        // 준비
        List<ProductResponse> result = List.of();
        when(productService.getSellingProduct()).thenReturn(result);

        // 실행 및 검증
        mockMvc.perform(
                        get("/api/v1/product/selling")
//                                .queryParam("name","이름") 얘는 쿼리파람이 있는경우

                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    // service 에서 이미 다검증이됐기때문에
    // 우리는 그냥 Array 가 잘오는지만 판단하면된다.
    @Test
    @DisplayName("신규 상품을 등록한다.")
    void createProduct() throws Exception {
        // 준비
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // 실행 및 검증
        mockMvc.perform(
                post("/api/v1/product/new")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("신규 상품을 등록할때 상품 타입은 필수값이다.")
    void createProductWithoutType() throws Exception {

        // 준비
        ProductCreateRequest request = ProductCreateRequest.builder()
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // 실행 및 검증
        mockMvc.perform(
                        post("/api/v1/product/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 타입은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}