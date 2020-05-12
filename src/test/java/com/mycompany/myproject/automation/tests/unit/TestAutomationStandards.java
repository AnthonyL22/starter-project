package com.mycompany.myproject.automation.tests.unit;

import com.pwc.core.framework.util.PropertiesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestAutomationStandards {

    private static final String WEB_TEST_DIRECTORY_LOCATION = "com/mycompany/myproject/automation/tests/web";
    private Collection<File> allFiles = new LinkedList<>();
    private Collection<File> allTestFiles = new LinkedList<>();

    @Before
    public void setUp() {

        File directory = PropertiesUtils.getFileFromResources(WEB_TEST_DIRECTORY_LOCATION);
        allFiles.addAll(FileUtils.listFiles(directory, new String[] {"class"}, true));

        allFiles.forEach(allFile -> {
            if (allFile.getName().contains("Test.class")) {
                allTestFiles.add(allFile);
            }
        });

    }

    @Test()
    public void testFirstLetterCapitalizedTest() {

        allTestFiles.forEach(testFile -> {
            String firstLetterInClassName = testFile.getName().substring(0, 1);
            Assert.assertTrue("Verify Test Name begins with CAPITAL for test='" + testFile.getName() + "'", StringUtils.isAllUpperCase(firstLetterInClassName));
        });

    }

    @Test()
    public void testNamedCorrectlyTest() {

        allTestFiles.forEach(testFile -> Assert.assertTrue("Verify Test Naming for test='" + testFile.getName() + "'", StringUtils.endsWith(testFile.getName(), "Test.class")));

    }

    @Test()
    public void testPackageNamingCapitalLetters() {

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "test-classes" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            boolean packageContainsUpperCaseChars = !packageName.equals(packageName.toLowerCase());
            Assert.assertFalse("Package name shouldn't contain capital letters.  Please review package='" + packageName + "'", packageContainsUpperCaseChars);
        });
    }

    @Test()
    public void testPackageNamingSpecialChars() {

        Pattern specialCharPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "test-classes" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            Matcher m = specialCharPattern.matcher(packageName);
            Assert.assertFalse("Package name shouldn't contain special chars.  Please review package='" + packageName + "'", m.find());
        });
    }

    @Test()
    public void testNameDuplicated() {

        allTestFiles.forEach(testFile -> {
            int numberOfTestsNamedIdentically = (int) allTestFiles.stream().filter(individual -> individual.getName().equals(testFile.getName())).count();
            Assert.assertEquals("Test Name should not be duplicated.  Please review test='" + testFile.getName() + "'", 1, numberOfTestsNamedIdentically);
        });
    }

    @Test
    public void testNameEndsWithTest() {

        allTestFiles.forEach(testFile -> assertTrue("Verify test ends with 'Test' suffix", StringUtils.endsWith(testFile.getName(), "Test.class")));

    }

    @Test
    public void testNameSameAsMethodName() {

        final String DELIMITER = "\u0001";

        allTestFiles.forEach(testFile -> {

            String expectedMethodName = testFile.getName();
            expectedMethodName = StringUtils.removeEnd(expectedMethodName, "Test.class");
            expectedMethodName = "test" + expectedMethodName;

            List<String> testContents = readCompiledClass(testFile);
            boolean foundMethodName = false;
            for (String testContent : testContents) {
                if (StringUtils.contains(testContent, expectedMethodName)) {
                    String[] methodContents = StringUtils.split(testContent, DELIMITER);
                    for (String methodContent : methodContents) {
                        if (StringUtils.containsIgnoreCase(methodContent, expectedMethodName)) {
                            String actualMethodName = "test" + StringUtils.substringAfter(methodContent, "test");
                            if (StringUtils.equalsIgnoreCase(expectedMethodName, actualMethodName)) {
                                foundMethodName = true;
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }

            Assert.assertTrue("Test method name matches and adheres to naming standards of class name for test='" + testFile.getName() + "'", foundMethodName);

        });

    }

    @Test
    public void testGherkinLoggingPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readCompiledClass(testFile);
            testContents.forEach(testContent -> {
                if (testContent.contains("LoggerService\u0001\u0000\u0007FEATURE")) {
                    assertTrue("FEATURE Gherkin logging is present for test='" + testFile.getName() + "'", testContent.contains("FEATURE"));
                    assertTrue("SCENARIO Gherkin logging is present for test='" + testFile.getName() + "'", testContent.contains("SCENARIO"));
                }
            });
        });

    }

    @Test
    public void testGroupNamesPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readCompiledClass(testFile);
            testContents.forEach(testContent -> {
                if (testContent.contains("groups")) {
                    assertTrue("TestNG Group annotation present for test='" + testFile.getName() + "'", testContent.contains("groups"));
                }
            });
        });

    }

    @Test
    public void testSystemOutPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readCompiledClass(testFile);
            testContents.forEach(testContent -> {
                if (testContent.contains("System") && testContent.contains("out")) {
                    assertFalse("JDK Native 'System' class usages present in test='" + testFile.getName() + "'", testContent.contains("System"));
                }
            });
        });

    }

    @Test
    public void testThreadSleepPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readCompiledClass(testFile);
            testContents.forEach(testContent -> {
                if (testContent.contains("Thread") && testContent.contains("sleep")) {
                    assertFalse("'Thread.sleep' is present in test='" + testFile.getName() + "'", testContent.contains("Thread") && testContent.contains("sleep"));
                }
            });
        });

    }

    /**
     * Read non-decompiled class contents.
     *
     * @param classFile class to read
     * @return non-decompiled class content
     */
    private List<String> readCompiledClass(File classFile) {
        return com.pwc.core.framework.util.FileUtils.readFile(classFile, classFile.getName());
    }

}
