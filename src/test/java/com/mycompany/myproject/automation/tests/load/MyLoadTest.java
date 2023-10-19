package com.mycompany.myproject.automation.tests.load;

import com.jayway.restassured.path.json.JsonPath;
import com.mycompany.myproject.automation.data.Constants;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import com.mycompany.myproject.automation.frameworksupport.command.webservice.WebServiceCommand;
import com.pwc.core.framework.FrameworkConstants;
import com.pwc.core.framework.util.DateUtils;
import com.pwc.core.framework.util.FileUtils;
import com.pwc.core.framework.util.PropertiesUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;

public class MyLoadTest extends MyApplicationTestCase {

    private JsonPath response;

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
    }

    @Override
    public void beforeMethod() {
        //Overridden
    }

    @Override
    public void afterMethod() {
        //Overridden
    }

    @Test()
    public void performLoad() {

        int readingOne = openBreedListPage();
        saveResults("LIST_BREEDS", readingOne);

        int readingTwo = openBreedDetailsPage();
        saveResults("BREED_DETAILS", readingTwo);

    }

    private int openBreedListPage() {

        int totalResponseTime;
        response = (JsonPath) webServiceAction(WebServiceCommand.GET_LIST_ALL_BREEDS);
        totalResponseTime = response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY);

        response = (JsonPath) webServiceAction(WebServiceCommand.GET_LIST_ALL_BREEDS);
        totalResponseTime = Integer.parseInt(response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY).toString()) + totalResponseTime;

        return totalResponseTime;
    }

    private int openBreedDetailsPage() {

        int totalResponseTime;
        response = (JsonPath) webServiceAction(WebServiceCommand.GET_LIST_ALL_BREEDS);
        totalResponseTime = response.get(FrameworkConstants.HTTP_RESPONSE_TIME_KEY);

        return totalResponseTime;
    }

    /**
     * Save a performance statistic to file for later reading by an external user.
     *
     * @param statistic statistic to store to file
     */
    private void saveResults(final String identifier, Object statistic) {

        String fileName = Constants.LOAD_TEST_RESULTS_DIRECTORY + LoadTestingHarness.TEST_RESULT_FILE;
        File stats = PropertiesUtils.getFileFromResources(fileName);
        if (stats == null) {
            FileUtils.createFile(fileName);
        }
        FileUtils.appendToFile(fileName, combine("%s, %s, %s\n", DateUtils.getDateTime("HH:mm:ss.SSS"), identifier, statistic));
    }

}
