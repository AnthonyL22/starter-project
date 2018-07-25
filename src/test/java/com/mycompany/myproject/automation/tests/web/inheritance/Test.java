package com.mycompany.myproject.automation.tests.web.inheritance;

public class Test {


    public static void main(String[] args){

        MountainBike mountainBike = new MountainBike(20, 4, "Black");
        System.out.println(mountainBike.getSeatHeight());
        System.out.println(mountainBike.getBikeColor());
        System.out.println(mountainBike.getNumberOfWheels());
    }

}
