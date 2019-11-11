package com.mycompany.myproject.automation.frameworksupport.command.webservice;

import com.pwc.core.framework.FrameworkConstants;

public enum WebServiceCommand implements com.pwc.core.framework.command.WebServiceCommand {

    GET_LIST_ALL_BREEDS(FrameworkConstants.GET_REQUEST, "api/breeds", "list/all");

    private String requestMethodType;
    private String requestMapping;
    private String methodName;

    WebServiceCommand(String type, String mapping, String name) {
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
