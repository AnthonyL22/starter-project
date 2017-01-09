package com.mycompany.myproject.automation.tests.web;

import com.mycompany.myproject.automation.frameworksupport.Groups;
import com.mycompany.myproject.automation.frameworksupport.MyApplicationTestCase;
import org.testng.annotations.Test;

public class BasicTest extends MyApplicationTestCase {

    @Override
    public void beforeMethod() {
    }

    @Override
    public void afterMethod() {
    }

    @Test(groups = {Groups.ACCEPTANCE_TEST})
    public void testBasic() {


    }

}
