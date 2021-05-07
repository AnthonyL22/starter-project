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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;

public class SourceAutomationStandards {

    private Collection<File> allJavaClasses = new LinkedList<>();

    @Before
    public void setUp() {

        Path resourceDirectory = Paths.get("src", "main", "java");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        File directory = new File(absolutePath);
        allJavaClasses = FileUtils.listFiles(directory, new String[] {"java"}, true).stream().filter(file -> !StringUtils.containsIgnoreCase(file.getAbsolutePath(), "idf"))
                        .collect(Collectors.toList());

    }

    @Test()
    public void testMoreThanZeroFilesScanned() {

        Assert.assertTrue("Verify > 0 files scanned/unit tested", allJavaClasses.size() > 0);

    }

    @Test()
    public void testNoJUnitUsage() {

        String toFind = "org.junit";
        allJavaClasses.forEach(javaFile -> {
            List<String> testContents = readClassContents(javaFile);
            Optional<String> foundMatch = testContents.stream().filter(line -> StringUtils.containsIgnoreCase(line, toFind)).findAny();
            Assert.assertFalse("Verify no JUnit usages in test classes class='" + javaFile.getName() + "'", foundMatch.isPresent());
        });

    }

    @Test()
    public void testNoDoubleSemiColon() {

        allJavaClasses.forEach(javaFile -> {
            List<String> testContents = readClassContents(javaFile);
            Optional<String> foundDouble = testContents.stream().filter(line -> StringUtils.containsIgnoreCase(line, ";;")).findAny();
            Assert.assertFalse("Verify no double semicolons for class='" + javaFile.getName() + "'", foundDouble.isPresent());
        });

    }

    @Test()
    public void testFirstLetterCapitalizedJavaClass() {

        allJavaClasses.forEach(javaFile -> {
            String firstLetterInClassName = javaFile.getName().substring(0, 1);
            Assert.assertTrue("Verify Class Name begins with CAPITAL for class='" + javaFile.getName() + "'", StringUtils.isAllUpperCase(firstLetterInClassName));
        });

    }

    @Test()
    public void testPackageNamingCapitalLetters() {

        allJavaClasses.forEach(javaFile -> {
            String packageName = StringUtils.substringBeforeLast(javaFile.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "src" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            boolean packageContainsUpperCaseChars = !packageName.equals(packageName.toLowerCase());
            assertFalse("Package name shouldn't contain capital letters.  Please review package='" + packageName + "'", packageContainsUpperCaseChars);
        });
    }

    @Test()
    public void testPackageNamingSpecialChars() {

        Pattern specialCharPattern = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        allJavaClasses.forEach(javaFile -> {
            String packageName = StringUtils.substringBeforeLast(javaFile.getAbsolutePath(), "/");
            packageName = StringUtils.substringAfter(packageName, "src" + File.separator);
            packageName = StringUtils.substringBeforeLast(packageName, File.separator);
            Matcher m = specialCharPattern.matcher(packageName);
            assertFalse("Package name shouldn't contain special chars.  Please review package='" + packageName + "'", m.find());
        });
    }

    @Test
    public void testSystemOutPresent() {

        allJavaClasses.forEach(javaFile -> {
            List<String> testContents = readClassContents(javaFile);
            boolean foundSystemOut = testContents.stream().anyMatch(code -> StringUtils.contains(code, "System") && StringUtils.contains(code, "out"));
            Assert.assertFalse("JDK Native 'System' class usages present in test='" + javaFile.getName() + "'", foundSystemOut);
        });

    }

    @Test
    public void testThreadSleepPresent() {

        allJavaClasses.forEach(javaFile -> {
            List<String> testContents = readClassContents(javaFile);
            boolean foundThreadSleep = testContents.stream().anyMatch(code -> StringUtils.contains(code, "Thread") && StringUtils.contains(code, "sleep"));
            Assert.assertFalse("'Thread.sleep' is present in test='" + javaFile.getName() + "'", foundThreadSleep);
        });

    }

    /**
     * Read non-decompiled class contents.
     *
     * @param javaFile class to read
     * @return non-decompiled class content
     */
    private List<String> readClassContents(File javaFile) {
        return com.pwc.core.framework.util.FileUtils.readFile(javaFile, javaFile.getName());
    }

}
