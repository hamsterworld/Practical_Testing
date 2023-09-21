package testCodeCourse.demo.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductResponse;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<ProductResponse> getSellingProduct(){
        List<Product> products = productRepository.findAllByProductSellingStatusIn(ProductSellingStatus.forDisplay());
        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

}
