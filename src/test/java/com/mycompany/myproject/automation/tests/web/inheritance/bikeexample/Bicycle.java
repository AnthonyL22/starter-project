package com.mycompany.myproject.automation.tests.web.inheritance.bikeexample;

public class Bicycle {

    private int numberOfWheels;
    private String bikeColor;

    public Bicycle(int wheelCount, String color) {
        this.numberOfWheels = wheelCount;
        this.bikeColor = color;
    }

    public int getNumberOfWheels() {
        return numberOfWheels;
    }

    public void setNumberOfWheels(int numberOfWheels) {
        this.numberOfWheels = numberOfWheels;
    }

    public String getBikeColor() {
        return bikeColor;
    }

    public void setBikeColor(String bikeColor) {
        this.bikeColor = bikeColor;
    }
}
