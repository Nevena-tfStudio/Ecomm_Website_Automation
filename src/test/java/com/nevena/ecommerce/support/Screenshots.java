package com.nevena.ecommerce.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;

/** Saves a screenshot of the browser to target/screenshots/ when a test does not pass. */
public final class Screenshots {
    private static final Path FOLDER = Path.of("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private Screenshots() {
    }

    /**
     * Captures the current browser window and logs where it was saved
     * (visible in the console and in the TestNG HTML report).
     * Never throws: a failed screenshot must not hide the real test failure.
     */
    public static void capture(WebDriver driver, ITestResult result) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.createDirectories(FOLDER);
            Path file = FOLDER.resolve(fileName(result));
            Files.write(file, png);
            // Also show it in the Allure report (under the test's tear-down step).
            Allure.attachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png),
                    AttachmentOptions.withFileExtension("png"));

            String message = "Screenshot: " + file.toAbsolutePath() + " (page: " + driver.getCurrentUrl() + ")";
            Reporter.setCurrentTestResult(result);
            Reporter.log(message, true); // true = also print to the console
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not save screenshot for " + result.getName() + ": " + e.getMessage());
        }
    }

    /** e.g. CheckoutTest.missingRequiredFieldShowsError[Jane_Doe_]-20261118-091502.png */
    private static String fileName(ITestResult result) {
        String testName = result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
        Object[] params = result.getParameters();
        if (params.length > 0) {
            testName += Arrays.stream(params).map(String::valueOf).collect(Collectors.joining("_", "[", "]"));
        }
        String safeName = testName.replaceAll("[^A-Za-z0-9._\\[\\]-]", "_");
        return safeName + "-" + LocalDateTime.now().format(TIMESTAMP) + ".png";
    }
}
