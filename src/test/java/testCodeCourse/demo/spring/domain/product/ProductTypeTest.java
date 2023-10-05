package testCodeCourse.demo.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductTypeTest {

    // 간단한것도 테스트해야되나요? 왜냐면 언제 바뀔지 모르기때문에 대비를 해둬야한다.
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType1() {
        ProductType givenType = ProductType.HANDMADE;

        boolean result = ProductType.containsStockType(givenType);

        assertThat(result).isFalse();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType2() {
        ProductType givenType = ProductType.BAKERY;

        boolean result = ProductType.containsStockType(givenType);

        assertThat(result).isTrue();
    }

    // 한문단에 한 주제
    // 아래처럼 테스트코드를 작성했다고 가정해보자.

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containStockTypeEx(){
        ProductType[] productTypes = ProductType.values();

        // containsStockType 에대한 테스트를 해보고싶엇던것같고
        // 모든 케이스를 여기다가 작성하려고했던것같다.
        // 분기문이나 반복문같은 논리구조가 들어간 테스트 자체가
        // 한문단에 2가지 이상의 관점을 포함하고있다는 반증이기도 하다.
        // 분기문 같은것도 2가지 이상을 테스트하겟다라는 것이다.
        // 반복문같은것도 테스트코드를 읽는사람이 생각을해야된다.
        // 한번더 논리구조가 들어간다.
        // 지금 이사람 테스트코드가 뭘하려는거지 라는 이해를 가지고들어가야한다.
        // 반복문에서 for 문을 열고 이 scope 안에서 productType 이 돌고있어
        // 라는게 머리속에 setting 이 되야한다.
        // 그래서 이런것들은 지양하는것이 좋다.
        // 한문단에는 한주제
        // 즉, disPlayName 을 한문장으로 구성할수있는가와도 비슷한 개념이다.
        for (ProductType productType : productTypes) {
            if(productType == ProductType.HANDMADE){
                // 실행
                boolean result = ProductType.containsStockType(productType);

                assertThat(result).isFalse();
            }

            if(productType == ProductType.BAKERY || productType == ProductType.BOTTLE){
                boolean result = ProductType.containsStockType(productType);
                assertThat(result).isTrue();
            }

        }


    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containStockType3(){
        ProductType givenType1 = ProductType.HANDMADE;
        ProductType givenType2 = ProductType.BOTTLE;
        ProductType givenType3 = ProductType.BAKERY;

        boolean result1 = ProductType.containsStockType(givenType1);
        boolean result2 = ProductType.containsStockType(givenType2);
        boolean result3 = ProductType.containsStockType(givenType3);

        assertThat(result1).isFalse();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @CsvSource({"HANDMADE,false","BOTTLE,true","BAKERY,true"})
    @ParameterizedTest
    void containStockType4(ProductType productType,boolean expected){

        // 실행
        boolean result = ProductType.containsStockType(productType);

        // 검증
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @MethodSource("provideProductTypesForCheckingStockType")
    @ParameterizedTest
    void containStockType5(ProductType productType,boolean expected){

        // 실행
        boolean result = ProductType.containsStockType(productType);

        // 검증
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideProductTypesForCheckingStockType(){
        return Stream.of(
                Arguments.of(ProductType.HANDMADE,false),
                Arguments.of(ProductType.BOTTLE,true),
                Arguments.of(ProductType.BAKERY,true)
        );
    }

}