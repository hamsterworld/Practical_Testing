package testCodeCourse.demo.spring.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import testCodeCourse.demo.spring.domain.product.ProductResponse;
import testCodeCourse.demo.spring.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/v1/products/selling")
    public List<ProductResponse> getSellingProducts(){
        return productService.getSellingProduct();
    }

}
