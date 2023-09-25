package testCodeCourse.demo.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testCodeCourse.demo.spring.domain.stock.Stock;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {

    List<Stock> findAllByProductNumberIn(List<String> productNumbers);

}
