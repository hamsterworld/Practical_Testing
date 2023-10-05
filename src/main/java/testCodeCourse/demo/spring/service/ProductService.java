package testCodeCourse.demo.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testCodeCourse.demo.spring.controller.product.request.ProductCreateRequest;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductResponse;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
/**
 * readOnly = true : 읽기전용
 * CRUD 에서 CUD 동작 X / only Read 만 가능하다.
 * JPA : CUD 스냅샷 저장, 변경감지 X (성능 향상)
 *
 * CQRS - Command / Read
 * 보통의 service 의 경우, Read 의 비율이 압도적으로 높다. 2:8 정도
 * 그래서 Command 와 Read 를 분리하자. 책임을 분리하자.
 * 만약에 사람들이 엄청몰려서 Read 를 해서 부하가 높아지고있다.
 * 그때 Command 가 같이 동작을 안해버리면 그것도 더큰 장애가 될수있다.
 * 반대도 그런 시나리오가 가능하다.
 * 그래서 우리는 Transactional 에서 Read = Only 는 신경써서 작성해야한다고 생각한다.
 * 그래서 Read = Only 를 사용하면 우리는 Command 용 서비스와 Read 용 서비를 분리할수있다.
 * 조회전용 서비스, CUD 전용서비스 이렇게 나누어서 개발한다.
 * 가장좋은것은 DB 에 관한 EndPoint 를 구분할수있다.
 * AWS mysql 같은경우 Read DB 랑 write DB 를 나누어서 사용하는 편이다.
 * 예를 들어서 마스터 와 슬레이브라고도 많이한다.
 * 마스터 = write , 그것의 replica 인 애는 read 용으로 많이 사용한다.
 * 그래서 write DB 에 접근할수있는 URL(endPoint) 와 Read DB 에 접근할수 있는 URL(endPoint) 요것을 분리해줄수있다.
 * 예를 들어서,
 * Transaction ReadOnly true 가 걸리면 읽기전용이니까 slave DB 로 보내자.
 * ReadOnly false 가 걸리면 CUD 니까 master DB 로 보내자.
 * DB endPoint 를 구분을 함으로써,장애 격리를 할수있다.
 * 그래서 이렇게 method 에 걸어줘서 구분을 해주는게 중요하다!
 * 결론, CUD 는 readOnly = false 를 주고
 * R 는 readOnly = false 를 주자.
 * 근데 method 마다 걸면 누락될수있다.
 * 그래서 서비스상단에 readOnly = true 를 걸고
 * CUD 작업을 하는 method 에 Transactional 을 걸어주자. 이렇게 하면 테스트할때 CUD 같은경우는 터질가능성이 높다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    // 아래처럼 이렇게 책임을 분리할수있다 private 메서드를 -> public 매서드로 분리.
    private final ProductNumberFactory productNumberFactory;

    @Transactional(readOnly = true)
    public List<ProductResponse> getSellingProduct(){
        List<Product> products = productRepository.findAllByProductSellingStatusIn(ProductSellingStatus.forDisplay());
        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    // 동시성 이슈
    // 해결방법
    // 1. DB 에 칼럼에 Unique 를 걸든가. => 이런경우에는 다시 시도를 하게한다던가.
    // 2. 정책을 변경한다면, UUID 도 괜찮다.
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        String nextProductNumber = productNumberFactory.createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

//    private String createNextProductNumber(){
//        String latesProductNumber = productRepository.findLatesProductNumber();
//        if(latesProductNumber == null){
//            return "001";
//        }
//
//        int latesProductNumberInt = Integer.parseInt(latesProductNumber);
//        int nextProductNumberInt = latesProductNumberInt + 1;
//
//        return String.format("%03d",nextProductNumberInt);
//    }
}
