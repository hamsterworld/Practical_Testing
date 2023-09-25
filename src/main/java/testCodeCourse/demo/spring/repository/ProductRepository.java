package testCodeCourse.demo.spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    /**
     *  select *
     *  from product
     *  where selling_type in ('SELLING','HOLD');
     */
    List<Product> findAllByProductSellingStatusIn(List<ProductSellingStatus> productSellingStatuses);

    List<Product> findAllByProductAllProductNumberIn(List<String> productNumbers);

    // 이렇게한것은 우리가 querymethod 든, nativeQuery 든어쨋든 repository 의 내부구현에 상관없이
    // 테스트가 먹혀야된다를 보여준것이다.
    @Query(value = "select p.product_number from product p order by id desc limit 1)",nativeQuery = true)
    String findLatesProductNumber();

}
