package testCodeCourse.demo.spring.service;

// private 매서드의 테스트는 어떻게 하나요? 에서파생된 클래스

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import testCodeCourse.demo.spring.repository.ProductRepository;

@Component
@RequiredArgsConstructor
public class ProductNumberFactory {

    private final ProductRepository productRepository;

    public String createNextProductNumber(){
        String latesProductNumber = productRepository.findLatesProductNumber();
        if(latesProductNumber == null){
            return "001";
        }

        int latesProductNumberInt = Integer.parseInt(latesProductNumber);
        int nextProductNumberInt = latesProductNumberInt + 1;

        return String.format("%03d",nextProductNumberInt);
    }

}
