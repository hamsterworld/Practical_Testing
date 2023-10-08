package testCodeCourse.demo.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import testCodeCourse.demo.spring.domain.order.Order;
import testCodeCourse.demo.spring.controller.order.request.OrderCreateRequest;
import testCodeCourse.demo.spring.domain.order.response.OrderResponse;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductType;
import testCodeCourse.demo.spring.domain.stock.Stock;
import testCodeCourse.demo.spring.repository.OrderRepository;
import testCodeCourse.demo.spring.repository.ProductRepository;
import testCodeCourse.demo.spring.repository.StockRepository;
import testCodeCourse.demo.spring.service.request.OrderCreateServiceRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

// 여기서도 체크해주고 왜 deductQuantity 내부에서도 체크해주는가?
// 서비스입장에서의 재고 체크를 해주는것
// 그리고 stock 내부의 deductQuantity 에서의 기능자체의 보장을 위해서 또한 내부에서도 체크해줘야한다.
// 관점을 다르게봐야한다.
// stock 자체의 매서드를 누군가가 사용할수있기때문에 그기능을 보장해줘야한다.
// 그리고 deductQuantity 는 Service Layer 의 기능을 몰라야한다.
// Message 자체도 다르게 줄수있다. service 에서 줄수있는지 메시지가 있고, stock 자체에서 줄수있는 메시지가있다.

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /**
     * 재고 감소 -> 동시성 고민
     * optimistic lock / pessimistic lock / ...
     */
    public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registerDateTime) {

        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);
        // Product.findProductsBy(products) 나라면 이렇게 했을수도?
        // 그러나 UtilClass 로도 뺄수있다.
        // 또는 ServiceClass 를 하나 더만들어서 주입받은후 사용할수도있다.

        deductStockQuantities(products);
        // Product.deductStockQuantities(products) 이렇게 해줬을수도?
        // 그러나 UtilClass 로도 뺄수있다.
        // 또는 ServiceClass 를 하나 더만들어서 주입받은후 사용할수도있다.

        Order order = Order.create(products, registerDateTime);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        
        // 재고 차감 체크가 필요함 상품들 filter
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        // 재고 엔티티 조회
        Map<String, Stock> stockMap = createStockMap(stockProductNumbers);

        // 상품별 counting
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        // 재고 차감 시도
        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();
            if(stock.isQuantityLessThan(quantity)){
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(quantity);
        }
        
    }

    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(toList());
    }

    private Map<String, Stock> createStockMap(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(toMap(Stock::getProductNumber, s -> s));
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(groupingBy(p -> p, counting()));
    }


    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductAllProductNumberIn(productNumbers);

        Map<String, Product> productMap = products.stream()
                .collect(toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                .map(productMap::get)
                .collect(toList());
    }

}
