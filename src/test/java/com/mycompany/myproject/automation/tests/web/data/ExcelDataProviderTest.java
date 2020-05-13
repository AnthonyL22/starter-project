package com.mycompany.myproject.automation.tests.web.data;

import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.data.provider.ExcelDataProvider;
import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.pwc.core.framework.annotations.MaxRetryCount;
import com.pwc.core.framework.listeners.Retry;
import org.testng.annotations.Test;

import static com.pwc.logging.service.LoggerService.FEATURE;
import static com.pwc.logging.service.LoggerService.GIVEN;
import static com.pwc.logging.service.LoggerService.LOG;
import static com.pwc.logging.service.LoggerService.SCENARIO;
import static com.pwc.logging.service.LoggerService.THEN;
import static com.pwc.logging.service.LoggerService.WHEN;

public class ExcelDataProviderTest extends MyApplicationTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @MaxRetryCount(1)
    @Test(retryAnalyzer = Retry.class, groups = {Groups.ACCEPTANCE_TEST}, dataProvider = "exampleExcelData", dataProviderClass = ExcelDataProvider.class)
    public void testExcelDataProvider(String firstName, String lastName, String nickName) {

        FEATURE("Feature Under Test");
        SCENARIO("Excel Sample Data Provider");
        GIVEN("I have done something");

        WHEN("I do something");
        webElementVisible(Constants.LOGO_ANCHOR_IMAGE);

        THEN("Something happens as expected");
        LOG(true, "First Name = %s", firstName);
        LOG(true, "Last Name = %s", lastName);
        LOG(true, "Nickname = %s", nickName);

    }

}
