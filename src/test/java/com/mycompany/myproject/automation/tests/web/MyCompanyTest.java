package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.JavascriptConstants;
import com.pwc.core.framework.annotations.Issue;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.*;

public class MyCompanyTest extends MyApplicationTestCase {

    private static final String SEARCH_TEXT = "pacificwebconsulting";
    String searchTextThatCanChange = "Anthony";

    @Override
    public void beforeMethod() {
        int sum = getCostSavingsByPrice(2, 4);
        System.out.println("The Cost Savings is = " + sum);
    }

    @Override
    public void afterMethod() {
    }

    @Issue("STORY-777")
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST})
    public void testMyCompany() {

        webElementVisible(Constants.LOGO_IMAGE);

        webAction(Constants.QUERY_INPUT, searchTextThatCanChange);

        //OR

        webAction(Constants.QUERY_INPUT, SEARCH_TEXT);

        webAction(Constants.SEARCH_BUTTON);
        webElementExists(Constants.CORE_ANCHOR);

        webAction(Constants.RUNNER_ANCHOR);

        String valueToSearch = getSearchText("microservice");
        webElementTextEquals(Constants.QUERY_INPUT, valueToSearch);

        // OR

        webElementTextEquals(Constants.QUERY_INPUT, "a:\"runner-microservice\"");

        webAction(Constants.ADVANCED_SEARCH_ANCHOR);
        webElementTextEquals(Constants.PACKAGING_INPUT, "");

        executeJavascript(JavascriptConstants.CLICK_BY_XPATH, Constants.ADVANCED_SEARCH_ANCHOR);
        webElementTextEquals(Constants.PACKAGING_INPUT, "");

    }

    private String getSearchText(final String name){

        // End Result should ==    a:"runner-microservice"
        String combinedQueryValue = "a:\"runner-" + name + "\"";
        return combinedQueryValue;
    }

    private int getCostSavingsByPrice(int originalPrice, int discountPrice) {

        int priceDifference = originalPrice - discountPrice;
        return priceDifference;
    }

}
