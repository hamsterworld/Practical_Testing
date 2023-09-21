package testCodeCourse.demo.unit;

import java.util.ArrayList;
import java.util.List;

public class CafeKiosk {

    private final List<Beverage> beverageList = new ArrayList<>();

    public void add(Beverage beverage){
        beverageList.add(beverage);
    }

    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Beverage beverage : beverageList) {
            totalPrice += beverage.getPrice();
        }
        return totalPrice;
    }
}
