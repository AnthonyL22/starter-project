package com.mycompany.myproject.automation.frameworksupport;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pwc.logging.service.LoggerService.LOG;

public abstract class ReportableTestCase extends MyApplicationTestCase {

    /**
     * Log a pretty spelling error to learn to file.
     *
     * @param outputFile File to create and output log information to
     * @param output     First element in comma separated line to identify the purpose
     * @param url        URL reference to include in log file
     * @return pretty log msg
     */
    protected void writeToLogFile(final File outputFile, final String output, final String url) {

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            String logOutput = writeToLogOutput(output, url);
            FileUtils.write(outputFile, logOutput, true);
        } catch (IOException e) {
            LOG(false, "", e);
        }

    }

    /**
     * Log a pretty spelling error to learn to file.
     *
     * @param outputFile        File to create and output log information to
     * @param purpose           First element in comma separated line to identify the purpose
     * @param url               URL reference to include in log file
     * @param specificTextToLog Text to log
     * @return pretty log msg
     */
    protected void writeToLogFile(final File outputFile, final String purpose, final String url, final Object specificTextToLog) {

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            String logOutput = writeToLogOutput(purpose, url, specificTextToLog);
            FileUtils.write(outputFile, logOutput, true);
        } catch (IOException e) {
            LOG(false, "", e);
        }

    }

    /**
     * Write the CSV file header before appending data to the comma separated file.
     *
     * @param orderedCsvHeadingList List of file headings
     * @param file                  file to add headers to
     */
    protected void writeHeaderToLogFile(List<String> orderedCsvHeadingList, File file) {

        try {
            AtomicInteger counter = new AtomicInteger();
            StringBuffer header = new StringBuffer();
            orderedCsvHeadingList.forEach(orderedCsvHeading -> {
                header.append(orderedCsvHeading);
                if (counter.get() + 1 < orderedCsvHeadingList.size()) {
                    header.append(",");
                }
                counter.getAndIncrement();
            });
            FileUtils.write(file, header.toString() + "\r\n", true);
        } catch (IOException e) {
            LOG(true, "Failed writing file header due to exception=%s", e);
        }
    }

    /**
     * Create comma-separated log output to potentially write to file.
     *
     * @param output First element in comma separated line to identify the purpose
     * @param url    URL reference to include in log file
     * @return hydrated value to log
     */
    private String writeToLogOutput(final String output, final String url) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(output);
            stringBuilder.append(",");
            stringBuilder.append(url);
            stringBuilder.append("\r\n");
        } catch (Exception e) {
            LOG(false, "", e);
        }
        return stringBuilder.toString();
    }

    /**
     * Create comma-separated log output to potentially write to file.
     *
     * @param purpose           First element in comma separated line to identify the purpose
     * @param url               URL reference to include in log file
     * @param specificTextToLog Text to log
     * @return hydrated value to log
     */
    private String writeToLogOutput(final String purpose, final String url, final Object specificTextToLog) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(purpose);
            stringBuilder.append(",");
            stringBuilder.append(specificTextToLog);
            stringBuilder.append(",");
            stringBuilder.append(url);
            stringBuilder.append("\r\n");
        } catch (Exception e) {
            LOG(false, "", e);
        }
        return stringBuilder.toString();
    }

}
