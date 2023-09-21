package testCodeCourse.demo.spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
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

}
