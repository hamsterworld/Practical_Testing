package testCodeCourse.demo.spring.controller.product.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import testCodeCourse.demo.spring.domain.product.Product;
import testCodeCourse.demo.spring.domain.product.ProductSellingStatus;
import testCodeCourse.demo.spring.domain.product.ProductType;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull (message = "상품 타입은 필수입니다.")
    private ProductType type;
    @NotNull (message = "상품 판매상태는 필수입니다.")
    private ProductSellingStatus sellingStatus;

    // 상품 이름은 20자제한
    // 그러나 이러한 validation 이 presentation 에서 챙겨야할게 맞나 생각을해보자.
    // String 에 대한 validation 에대해서는 굉장히 많을수있다.
    // 강의하는 사람은 blank 정도만 하는게 괜찮다고 생각한다.
    // 20자 검증은 좀더 안쪽 layer 에서 검증하는게 맞다고 생각한다.
    @Max(20)
    @NotBlank (message = "상품 이름은 필수입니다.")
    // String 에서
    // NotBlank 전부다 통과 x
    // NotNull  "" "   " 통과
    // NotEmpty "   " 통과
    private String name;
    @Positive (message = "상품 가격은 양수여야 합니다.")
    private int price;

    @Builder
    private ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }


    public Product toEntity(String nextProductNumber) {
        return Product.builder()
                .productNumber(nextProductNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }
}
