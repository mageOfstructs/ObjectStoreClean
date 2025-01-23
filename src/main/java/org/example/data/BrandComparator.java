package org.example.data;

import java.util.Comparator;

public class BrandComparator implements Comparator<Car> {
    @Override
    public int compare(Car car, Car t1) {
        return car.getManufacturer().compareTo(t1.getManufacturer());
    }
}
