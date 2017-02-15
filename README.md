Got a question?  [Email us](http://www.pacificwebconsulting.com/contact/) or reach out on [LinkedIn](https://www.linkedin.com/in/alombardo/) 

# Introduction
Starter project for new automated testing initiative.  This is a project to simply clone and start building your
automated testing solution immediately with a few simple configurations detailed below.

# Questions and issues

The [github issue tracker](https://github.com/AnthonyL22/starter-project/issues) is **_only_** for bug reports and 
feature requests. Anything else, such as questions for help in using the library, should be [emailed to our team](http://www.pacificwebconsulting.com/contact/).  

# Prerequisites

1. Java 1.8
2. Maven 3.x
3. Chrome

# Quick Start

Once the prerequisites are installed and configured follow these 3 simple instructions to get your test solution
up and running:

1. [Clone this Starter Project](https://github.com/AnthonyL22/starter-project.git)
2. Open command prompt (Unix users use sudo access if possible)
3. Perform *mvn clean install -Psingle -Dtest.name=BasicTest -Dtest.env=test* on this project

## To Run in IntelliJ
1. Open IntelliJ
2. Add default runtime configuration for TestNG VM option of *-Dtest.env=test*
3. Run test in Debug mode.

## Customize To Your Application
1. Open the *automation.properties* file, change the *web.url* property to point to your AUT
2. In a browser, open your AUT login or entry page.  Note a common element on this entry page.
3. Open Constants.java; Create an interface entry such as *String LOGIN_BUTTON = "Login";*
4. Open the MyApplicationTestCase.java class; Replace line 26 with your addition from previous Step
5. In IntelliJ, verify you have a default TestNG VM option of *-Dtest.env=test*
6. Run test in Debug mode.

# Configuration
## Assertion Handling
* By default, all tests are run in Soft Assertions mode (test will continue executing if failure occurs)
* You can override this setting in the automation.properties file by setting the enable.hard.assert=false

## Dynamic Properties
-Dtest.user=YOUR_USERNAME

-Dtest.password=YOUR_PASSWORD

-Dtest.env=YOUR ENVIRONMENT_CONFIGURATION

-Dbrowser=YOUR_BROWSER

-Dbrowser.version=YOUR_BROWSER_VERSION

-Dbrowser.resolution=YOUR_BROWSER_RESOLUTION

-Dplatform=YOUR_OPERATING_SYSTEM

-Dtest.group=YOUR_TESTNG_GROUP

# Test Execution
Executing any test requires defining the **-Dtest.env=dev,test,etc...** runtime property.  This is necessary to support executing any and all scripts
against any environment desired.

## IDE
In order to execute tests in your Integrated Development Environment you must simply add a TestNG variable to tell
the automated tests which environment to run against.
 
In your IDE's Runtime Configuration for TestNG, simply add **-Dtest.env=ENVIRONMENT

### Run Using SauceLabs
If you have a SauceLabs account, define the **grid.hub.url** in your grid.properties file according to the settings
defined in the setup instructions provided by SauceLabs.

Be sure to download and run the [Sauce Connect plugin](https://docs.saucelabs.com/reference/sauce-connect/) in your 
local environment to execute your tests in SauceLabs.

Add the following section to your **settings.xml** in order to connect your local Maven profile to SauceLabs:
```
<profile>
    <id>saucelabs</id>
    <properties>
        <sauce.username>YOUR_SAUCELABS_USER</sauce.username>
        <sauce.key>YOUR_SAUCELABS_KEY</sauce.key>
    </properties>
</profile>
```

## Maven
### Run Using SauceLabs
$ mvn clean install -Psaucelabs -PGROUP_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Psaucelabs -Pacceptance -Dtest.env=test
```

### Run Single Test
$ mvn clean install -Psingle -Dtest.name=TEST_CLASS_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Psingle -Dtest.name=BasicTest -Dtest.env=test
```

### Run a Maven Profile of Tests
$ mvn clean install -PGROUP_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Pacceptance -Dtest.env=test
```

### Run ANY Group of Test(s)
$ mvn clean install -Pgroup -Dtest.group=GROUP_NAME -Dtest.env=ENVIRONMENT 

```
mvn clean install -Pgroup -Dtest.group=webServiceTest -Dtest.env=test
```

### Run ANY Test(s)
$ mvn clean install -Pgroup -Dtest.group=GROUP_NAME -Dtest.env=ENVIRONMENT -Dtest.package=PACKAGE_NAME -Dtest.threadCount=NUMBER_OF_THREADS 

```
mvn clean install -Pgroup -Dtest.group=webServiceTest -Dtest.env=test -Dtest.package=* -Dtest.threadCount=1
```

### Run Manual Tests
Executes all manual tests stubbed out ready to be automated.  The artifact of executing these tests is the Gherkin logging for the test scenario
that can then be used as instructions to performing your manual tests.

$ mvn clean install -Pmanual -Dtest.env=ENVIRONMENT

```
mvn clean install -Pmanual -Dtest.env=test
```
