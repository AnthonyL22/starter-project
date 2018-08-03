package com.mycompany.myproject.automation.tests.web.inheritance.parentexample;

public class Test {


    public static void main(String[] args) {

        Anthony whoIsAnthony = new Anthony(70, "Surfing", "New Zealand");
        System.out.println(whoIsAnthony.getHowTall());
        System.out.println(whoIsAnthony.getPersonLikes());
        System.out.println(whoIsAnthony.getHeritage());
    }

}
