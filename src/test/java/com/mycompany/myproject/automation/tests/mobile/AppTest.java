package com.mycompany.myproject.automation.tests.mobile;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class AppTest {

    public static URL url;
    public static DesiredCapabilities capabilities;
    public static IOSDriver<IOSElement> driver;

    @BeforeSuite
    public void setupAppium() throws MalformedURLException {

        final String URL_STRING = "http://127.0.0.1:4723/wd/hub";
        url = new URL(URL_STRING);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13.1");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 8");
        capabilities.setCapability(MobileCapabilityType.APP, "/Users/anthonylombardo/workspace/anthony-sample-app/anthony/build/Release-iphonesimulator/anthony.app");
        capabilities.setCapability(MobileCapabilityType.APP, "settings");
        capabilities.setCapability("useNewWDA", false);

        driver = new IOSDriver(url, capabilities);
        //        driver.activateApp("pwc.anthony");
        //driver.activateApp("com.apple.MobileSMS");
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        /**
         * 1. How do I launch my app?
         * 2. How do I run my test
         * 3. How do I start interacting with the app
         */


    }

    @AfterTest
    public void uninstallApp() {
        driver.removeApp("pwc.anthony");
        driver.resetApp();
    }

    @Test()
    public void myFirstTest() {
        //driver.resetApp();
        //driver.activateApp("pwc.anthony");

        /**
         * framework ideas
         *
         * - if I see '==' I know it's a findElementByIosNsPredicate
         * - if I see xpath regex then use driver.findElement(By.xpath
         */

        MobileElement element = driver.findElementByIosNsPredicate("type == 'XCUIElementTypeStaticText' and name == 'General'");
        element.click();
        MobileElement settings = driver.findElementByIosNsPredicate("type == 'XCUIElementTypeApplication' and name == 'Settings'");
        settings.click();
        MobileElement elementz = driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='General']"));
        element.click();

        driver.findElement(MobileBy.iOSNsPredicateString("value BEGINSWITH[G]"));
        driver.findElement(MobileBy.AccessibilityId("Cancel")).isDisplayed();
        System.out.println("Performing my test now");


    }

}
