package com.mycompany.myproject.automation.frameworksupport.command.webservice;

import com.pwc.core.framework.FrameworkConstants;

public enum TaylorMadeCommand implements com.pwc.core.framework.command.WebServiceCommand {

    POST_RECOMMENDED_START(FrameworkConstants.POST_REQUEST, "default", "CQRecomm-Start"),
    GET_POWER_REVIEWS(FrameworkConstants.GET_REQUEST, "default", "PowerReviews-GetReviews"),
    GET_PRODUCT_AVAILABILITY(FrameworkConstants.GET_REQUEST, "default", "Product-GetAvailability");

    private String requestMethodType;
    private String requestMapping;
    private String methodName;

    TaylorMadeCommand(String type, String mapping, String name) {
        requestMethodType = type;
        requestMapping = mapping;
        methodName = name;
    }

    @Override
    public String methodType() {
        return this.requestMethodType;
    }

    @Override
    public String mappingName() {
        return this.requestMapping;
    }

    @Override
    public String methodName() {
        return this.methodName;
    }

}
