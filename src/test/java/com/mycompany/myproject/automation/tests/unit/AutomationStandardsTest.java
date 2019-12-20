package com.mycompany.myproject.automation.tests.unit;

import com.pwc.core.framework.util.PropertiesUtils;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutomationStandardsTest {

    private static final String DECOMPILER_DELIMITER = "\u0001";
    private static final String WEB_TEST_DIRECTORY_LOCATION = "com/mycompany/myproject/automation/tests/web";
    private Collection<File> allFiles = new LinkedList<>();
    private Collection<File> allTestFiles = new LinkedList<>();

    @Before
    public void setUp() {

        File directory = PropertiesUtils.getFileFromResources(WEB_TEST_DIRECTORY_LOCATION);
        allFiles.addAll(FileUtils.listFiles(directory, new String[] {"class"}, true));

        for (File allFile : allFiles) {
            if (allFile.getName().contains("Test.class")) {
                allTestFiles.add(allFile);
            }
        }

    }

    @After
    public void tearDown() {
    }

    @Test()
    public void testFirstLetterCapitalizedTest() {

        allTestFiles.forEach(testFile -> {
            String firstLetterInClassName = testFile.getName().substring(0, 1);
            Assert.assertTrue("Class name should begin with a Capital Letter.  Please review test='" + testFile.getName() + "'", StringUtils.isAllUpperCase(firstLetterInClassName));
        });

    }

    @Test()
    public void testPackageNamingCapitalLetters() {

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "java");
            boolean packageContainsUpperCaseChars = !packageName.equals(packageName.toLowerCase());
            Assert.assertFalse("Package name shouldn't contain capital letters.  Please review package='" + packageName + "'", packageContainsUpperCaseChars);
        });
    }

    @Test()
    public void testPackageNamingSpecialChars() {

        Pattern specialCharPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "java");
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

        allTestFiles.forEach(testFile -> Assert.assertTrue("Verify test ends with 'Test' suffix", StringUtils.endsWith(testFile.getName(), "Test.class")));

    }

    @Test
    public void testNameSameAsMethodName() {

        allTestFiles.forEach(testFile -> {

            String expectedMethodName = testFile.getName();
            expectedMethodName = StringUtils.removeEnd(expectedMethodName, "Test.class");
            expectedMethodName = "test" + expectedMethodName;

            List<String> testContents = readCompiledClass(testFile);
            boolean foundMethodName = false;
            for (String testContent : testContents) {
                if (StringUtils.contains(testContent, expectedMethodName)) {
                    String[] methodContents = StringUtils.split(testContent, DECOMPILER_DELIMITER);
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
                    Assert.assertTrue("FEATURE Gherkin logging is present for test='" + testFile.getName() + "'", testContent.contains("FEATURE"));
                    Assert.assertTrue("SCENARIO Gherkin logging is present for test='" + testFile.getName() + "'", testContent.contains("SCENARIO"));
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
                    Assert.assertTrue("TestNG Group annotation present for test='" + testFile.getName() + "'", testContent.contains("groups"));
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
                    Assert.assertFalse("JDK Native 'System' class usages present in test='" + testFile.getName() + "'", testContent.contains("System"));
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
                    Assert.assertFalse("'Thread.sleep' is present in test='" + testFile.getName() + "'", testContent.contains("Thread") && testContent.contains("sleep"));
                }
            });
        });

    }

    /**
     * Read the compiled version of a class file.
     *
     * @param testFile class File to read
     * @return <code>String</code> representation of .class file
     */
    private List<String> readCompiledClass(File testFile) {
        return com.pwc.core.framework.util.FileUtils.readFile(testFile, testFile.getName());
    }

    /**
     * Find a method name in a class's contents
     *
     * @param testContents     class contents as List
     * @param methodNameToFind method name to find
     * @return delimited list by special decompiler character
     */
    private List<String> checkTestContentForMethodName(final List<String> testContents, final String methodNameToFind) {

        for (String testContent : testContents) {
            if (StringUtils.contains(testContent, methodNameToFind)) {
                return Arrays.asList(StringUtils.split(testContent, DECOMPILER_DELIMITER));
            }
        }
        return new ArrayList<>();
    }

    /**
     * Read and decompile class contents into readable <code>String[]</code>.
     *
     * @param classFile class to decompile
     * @return String array of decompiled code
     * @throws IOException decompile exception
     */
    private String[] decompileClassToArray(File classFile) throws IOException {

        String OPEN = "\"";
        String CLOSE = "\"";
        String[] stringsArray;
        final StringWriter stringWriter = new StringWriter();

        try {
            final DecompilerSettings settings = DecompilerSettings.javaDefaults();
            settings.setIncludeErrorDiagnostics(true);
            Decompiler.decompile(classFile.getPath(), new PlainTextOutput(stringWriter), settings);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stringsArray = StringUtils.substringsBetween(stringWriter.getBuffer().toString(), OPEN, CLOSE);
            stringWriter.close();
        }

        return stringsArray;
    }

}
