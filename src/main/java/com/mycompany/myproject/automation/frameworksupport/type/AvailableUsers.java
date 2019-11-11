package com.mycompany.myproject.automation.frameworksupport.type;

import java.util.List;

public class AvailableUsers {

    private String environment;
    private List<Account> accounts;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

}
