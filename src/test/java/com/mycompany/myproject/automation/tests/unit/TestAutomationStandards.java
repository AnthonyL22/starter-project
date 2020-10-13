package com.mycompany.myproject.automation.tests.unit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestAutomationStandards {

    private Collection<File> allTestFiles = new LinkedList<>();

    @Before
    public void setUp() {

        Path resourceDirectory = Paths.get("src", "test", "java");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        File directory = new File(absolutePath);
        allTestFiles = FileUtils
                        .listFiles(directory, new String[] {"java"}, true).stream().filter(file -> !StringUtils.containsIgnoreCase(file.getAbsolutePath(), "unit")
                                        && !StringUtils.containsIgnoreCase(file.getAbsolutePath(), "load") && !StringUtils.containsIgnoreCase(file.getAbsolutePath(), "headless"))
                        .collect(Collectors.toList());
    }

    @Test
    public void testFirstLetterCapitalizedTest() {

        allTestFiles.forEach(testFile -> {
            String firstLetterInClassName = testFile.getName().substring(0, 1);
            Assert.assertTrue("Class name should begin with a Capital Letter.  Please review test='" + testFile.getName() + "'", StringUtils.isAllUpperCase(firstLetterInClassName));
        });

    }

    @Test
    public void testPackageNamingCapitalLetters() {

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "java" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            boolean packageContainsUpperCaseChars = !packageName.equals(packageName.toLowerCase());
            Assert.assertFalse("Package name shouldn't contain capital letters.  Please review package='" + packageName + "'", packageContainsUpperCaseChars);
        });
    }

    @Test
    public void testPackageNamingSpecialChars() {

        Pattern specialCharPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        allTestFiles.forEach(individual -> {
            String packageName = StringUtils.substringBeforeLast(individual.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "java" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            Matcher m = specialCharPattern.matcher(packageName);
            Assert.assertFalse("Package name shouldn't contain special chars.  Please review package='" + packageName + "'", m.find());
        });

    }

    @Test
    public void testNameDuplicated() {

        allTestFiles.forEach(testFile -> {
            int numberOfTestsNamedIdentically = (int) allTestFiles.stream().filter(individual -> individual.getName().equals(testFile.getName())).count();
            Assert.assertEquals("Test Name should not be duplicated.  Please review test='" + testFile.getName() + "'", 1, numberOfTestsNamedIdentically);
        });

    }

    @Test
    public void testNameEndsWithTest() {

        allTestFiles.forEach(testFile -> Assert.assertTrue("Verify test ends with 'Test' suffix", StringUtils.endsWith(testFile.getName(), "Test.java")));

    }

    @Test
    public void testNameSameAsMethodName() {

        allTestFiles.forEach(testFile -> {

            String expectedMethodName = testFile.getName();
            expectedMethodName = StringUtils.removeEnd(expectedMethodName, "Test.java");
            expectedMethodName = "test" + expectedMethodName + "(";

            List<String> testContents = readClassContents(testFile);
            String finalExpectedMethodName = expectedMethodName;
            boolean foundExpectedMethodName = testContents.stream().anyMatch(code -> StringUtils.contains(code, finalExpectedMethodName));
            Assert.assertTrue("Test method name matches and adheres to naming standards of class name for test='" + testFile.getName() + "'", foundExpectedMethodName);

        });

    }

    @Test
    public void testGherkinLoggingPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readClassContents(testFile);
            boolean foundFeature = testContents.stream().anyMatch(code -> StringUtils.contains(code, "FEATURE"));
            Assert.assertTrue("FEATURE Gherkin logging is present for test='" + testFile.getName() + "'", foundFeature);
            boolean foundScenario = testContents.stream().anyMatch(code -> StringUtils.contains(code, "SCENARIO"));
            Assert.assertTrue("SCENARIO Gherkin logging is present for test='" + testFile.getName() + "'", foundScenario);
            boolean foundGiven = testContents.stream().anyMatch(code -> StringUtils.contains(code, "GIVEN"));
            Assert.assertTrue("GIVEN Gherkin logging is present for test='" + testFile.getName() + "'", foundGiven);
            boolean foundWhen = testContents.stream().anyMatch(code -> StringUtils.contains(code, "WHEN"));
            Assert.assertTrue("WHEN Gherkin logging is present for test='" + testFile.getName() + "'", foundWhen);
            boolean foundThen = testContents.stream().anyMatch(code -> StringUtils.contains(code, "THEN"));
            Assert.assertTrue("THEN Gherkin logging is present for test='" + testFile.getName() + "'", foundThen);
        });

    }

    @Test
    public void testDeclarativeGherkin() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readClassContents(testFile);
            long numberOfFeatures = IntStream.range(0, testContents.size()).filter(index -> StringUtils.contains(testContents.get(index), "FEATURE(")).count();
            Assert.assertEquals("FEATURE Gherkin logging is present for test='" + testFile.getName() + "'", numberOfFeatures, 1);
            long numberOfScenarios = IntStream.range(0, testContents.size()).filter(index -> StringUtils.contains(testContents.get(index), "SCENARIO(")).count();
            Assert.assertEquals("SCENARIO Gherkin logging is present for test='" + testFile.getName() + "'", numberOfScenarios, 1);
            long numberOfGivens = IntStream.range(0, testContents.size()).filter(index -> StringUtils.contains(testContents.get(index), "GIVEN(")).count();
            Assert.assertEquals("GIVEN Gherkin logging is present for test='" + testFile.getName() + "'", numberOfGivens, 1);
            long numberOfWhens = IntStream.range(0, testContents.size()).filter(index -> StringUtils.contains(testContents.get(index), "WHEN(")).count();
            Assert.assertEquals("WHEN Gherkin logging is present for test='" + testFile.getName() + "'", numberOfWhens, 1);
            long numberOfThens = IntStream.range(0, testContents.size()).filter(index -> StringUtils.contains(testContents.get(index), "THEN(")).count();
            Assert.assertEquals("THEN Gherkin logging is present for test='" + testFile.getName() + "'", numberOfThens, 1);
        });

    }

    @Test
    public void testGroupNamesPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readClassContents(testFile);
            boolean foundTestNGGroupAnnotation = testContents.stream().anyMatch(code -> StringUtils.contains(code, "groups"));
            Assert.assertTrue("TestNG Group annotation present for test='" + testFile.getName() + "'", foundTestNGGroupAnnotation);
        });

    }

    @Test
    public void testSystemOutPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readClassContents(testFile);
            boolean foundSystemOut = testContents.stream().anyMatch(code -> StringUtils.contains(code, "System") && StringUtils.contains(code, "out"));
            Assert.assertFalse("JDK Native 'System' class usages present in test='" + testFile.getName() + "'", foundSystemOut);
        });

    }

    @Test
    public void testThreadSleepPresent() {

        allTestFiles.forEach(testFile -> {
            List<String> testContents = readClassContents(testFile);
            boolean foundThreadSleep = testContents.stream().anyMatch(code -> StringUtils.contains(code, "Thread") && StringUtils.contains(code, "sleep"));
            Assert.assertFalse("'Thread.sleep' is present in test='" + testFile.getName() + "'", foundThreadSleep);
        });

    }

    /**
     * Read the Java file contents.
     *
     * @param testFile Java File to read
     * @return <code>String</code> representation of .java file
     */
    private List<String> readClassContents(File testFile) {
        return com.pwc.core.framework.util.FileUtils.readFile(testFile, testFile.getName());
    }

}
