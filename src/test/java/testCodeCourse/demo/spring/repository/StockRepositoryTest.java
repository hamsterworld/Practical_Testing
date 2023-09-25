package testCodeCourse.demo.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.domain.stock.Stock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("상품번호 리스트로 재고를 조회한다.")
    @Test
    void findAllByProductAllProductNumberIn() {
        // 준비
        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 3);
        stockRepository.saveAll(List.of(stock1,stock2,stock3));


        // 실행
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(List.of("001", "002"));


        // 검증
        assertThat(stocks).hasSize(2)
                .extracting("ProductName","quantity")
                .containsExactlyInAnyOrder(
                        tuple("001",1),
                        tuple("002",2)
                );
    }
}