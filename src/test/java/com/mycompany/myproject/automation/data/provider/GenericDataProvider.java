package com.mycompany.myproject.automation.data.provider;

import com.pwc.core.framework.util.RandomStringUtils;
import org.testng.annotations.DataProvider;

public class GenericDataProvider {

    @DataProvider(name = "genericSearchText")
    public static Object[][] genericSearchText() {
        return new Object[][]{
                {RandomStringUtils.randomAlphabetic(10)},
                {RandomStringUtils.randomAlphanumeric(10)},
                {RandomStringUtils.randomXML()},
                {RandomStringUtils.randomHTML(10, true)},
                {RandomStringUtils.randomNumeric(10)}};
    }

}
