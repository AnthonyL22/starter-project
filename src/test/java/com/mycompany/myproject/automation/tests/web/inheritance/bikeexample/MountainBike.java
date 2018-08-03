package com.mycompany.myproject.automation.tests.web.inheritance.bikeexample;

public class MountainBike extends Bicycle {

    public int seatHeight;

    public MountainBike(int height, int wheelCount, String color) {
        super(wheelCount, color);
        this.seatHeight = height;
    }

    public int getSeatHeight() {
        return seatHeight;
    }

    public void setSeatHeight(int seatHeight) {
        this.seatHeight = seatHeight;
    }

}
