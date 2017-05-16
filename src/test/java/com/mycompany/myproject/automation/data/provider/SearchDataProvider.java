package com.mycompany.myproject.automation.data.provider;

import org.testng.annotations.DataProvider;

public class SearchDataProvider {

    @DataProvider(name = "basicSearchText")
    public static Object[][] basicSearchText() {
        return new Object[][]{
                {"pacificwebconsulting"}};
    }

}
