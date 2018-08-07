package com.mycompany.myproject.automation.tests.web.inheritance.parentexample;

public class Parents {

    private String heritage;
    private final String eyeColor;
    private final String DEFAULT_EYE_COLOR_FOR_ALL_LOMBARDOS = "Hazel";

    public Parents(String whereImFrom) {
        this.heritage = whereImFrom;
        this.eyeColor = DEFAULT_EYE_COLOR_FOR_ALL_LOMBARDOS;
    }

    public String getHeritage() {
        return heritage;
    }

    public void setHeritage(String heritage) {
        this.heritage = heritage;
    }

}
