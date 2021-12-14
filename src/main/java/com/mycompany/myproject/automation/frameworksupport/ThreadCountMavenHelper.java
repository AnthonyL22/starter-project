package com.mycompany.myproject.automation.frameworksupport;

import com.pwc.core.framework.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.pwc.logging.service.LoggerService.LOG;

public class ThreadCountMavenHelper {

    /**
     * Maven helper program to change the TestNG failed thread-count variable to '1' or whatever count
     * desired to help with re-running failed tests from Maven
     *
     * @param args thread-count number to set in testng-failed.xml file
     */
    public static void main(String[] args) {

        String threadCount = "1";
        try {

            threadCount = args[0];
            LOG(true, "Setting TestNG Rerun Failed Thread-Count=%s", threadCount);

            File testNgFailedFile = new File("target/failsafe-reports/testng-failed.xml");
            if (testNgFailedFile.exists()) {
                List<String> contentListToWrite = new ArrayList<>();
                List<String> contents = FileUtils.readFile(testNgFailedFile);
                String finalThreadCount = threadCount;
                contents.forEach(line -> {
                    if (StringUtils.containsIgnoreCase(line, "thread-count")) {
                        String activeLine = StringUtils.replacePattern(line, "thread-count=\"\\d+\"", String.format("thread-count=\"%s\"", finalThreadCount));
                        contentListToWrite.add(activeLine);
                    } else if (StringUtils.containsIgnoreCase(line, "<suite")) {
                        String activeLine = StringUtils.replacePattern(line, "\\<suite\\s", String.format("<suite thread-count=\"%s\" ", finalThreadCount));
                        contentListToWrite.add(activeLine);
                    } else {
                        contentListToWrite.add(line);
                    }
                });
                FileUtils.deleteFile(testNgFailedFile, "testng-failed.xml");
                org.apache.commons.io.FileUtils.writeLines(testNgFailedFile, contentListToWrite);
            } else {
                LOG(true, "Skipping Helper %s", "Utility");
            }

        } catch (IOException e) {
            LOG(true, "Failed to write new 'testng-failed.xml with thread-count=%s due to e=%s", threadCount, e);
        }

    }

}
