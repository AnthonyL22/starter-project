## Introduction
Starter project for new automation initiative

## Prerequisites

1. Java 1.8
2. Maven 3.x

## Source Control

[GIT Project location](https://github.com)

## Jenkins

[Jenkins Jobs](https://github.com)

[Test Pipeline](https://github.com)

## Build
To compile and verify your project is configured correctly simply build with the following command.

```
mvn clean install
```

## Configuration
### Assertion Handling
* By default, all tests are run in Soft Assertions mode (test will continue executing if failure occurs)
* You can override this setting in the automation.properties file by setting the enable.hard.assert=false

### Dynamic Properties
-Dtest.user=YOUR_USERNAME

-Dtest.password=YOUR_PASSWORD

-Dtest.env=YOUR ENVIRONMENT_CONFIGURATION

-Dbrowser=YOUR_BROWSER

-Dbrowser.version=YOUR_BROWSER_VERSION

-Dbrowser.resolution=YOUR_BROWSER_RESOLUTION

-Dplatform=YOUR_OPERATING_SYSTEM

-Dtest.group=YOUR_TESTNG_GROUP

## Test Execution
Executing any test requires defining the **-Dtest.env=dev,test,etc...** runtime property.  This is necessary to support executing any and all scripts
against any environment desired.

### IDE
In order to execute tests in your Integrated Development Environment you must simply add a TestNG variable to tell
the automated tests which environment to run against.
 
In your IDE's Runtime Configuration for TestNG, simply add **-Dtest.env=ENVIRONMENT

#### Run Using SauceLabs
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

### Maven
#### Run Using SauceLabs
$ mvn clean install -Psaucelabs -PGROUP_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Psaucelabs -Pacceptance -Dtest.env=test
```

#### Run Single Test
$ mvn clean install -Psingle -Dtest.name=TEST_CLASS_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Psingle -Dtest.name=BasicTest -Dtest.env=test
```

#### Run a Maven Profile of Tests
$ mvn clean install -PGROUP_NAME -Dtest.env=ENVIRONMENT

```
mvn clean install -Pacceptance -Dtest.env=test
```

#### Run ANY Group of Test(s)
$ mvn clean install -Pgroup -Dtest.group=GROUP_NAME -Dtest.env=ENVIRONMENT 

```
mvn clean install -Pgroup -Dtest.group=webServiceTest -Dtest.env=test
```

#### Run ANY Test(s)
$ mvn clean install -Pgroup -Dtest.group=GROUP_NAME -Dtest.env=ENVIRONMENT -Dtest.package=PACKAGE_NAME -Dtest.threadCount=NUMBER_OF_THREADS 

```
mvn clean install -Pgroup -Dtest.group=webServiceTest -Dtest.env=test -Dtest.package=* -Dtest.threadCount=1
```

#### Run Manual Tests
Executes all manual tests stubbed out ready to be automated.  The artifact of executing these tests is the Gherkin logging for the test scenario
that can then be used as instructions to performing your manual tests.

$ mvn clean install -Pmanual -Dtest.env=ENVIRONMENT

```
mvn clean install -Pmanual -Dtest.env=test
```