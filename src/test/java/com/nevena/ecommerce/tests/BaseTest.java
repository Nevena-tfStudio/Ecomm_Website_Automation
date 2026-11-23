package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.support.DriverFactory;
import com.nevena.ecommerce.support.Screenshots;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Starts a fresh browser before each test and closes it afterwards.
 * Every test class extends this. alwaysRun = true keeps setup running
 * when a suite selects tests by group (smoke/regression).
 *
 * Tests run in parallel, and TestNG shares one instance of a test class between
 * threads. So the browser is kept per thread (ThreadLocal), never in a plain field:
 * always use driver().
 */
public abstract class BaseTest {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /** The browser belonging to the test running on this thread. */
    protected WebDriver driver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No browser for this thread; did startBrowser() run?");
        }
        return driver;
    }

    /** Sets the browser window to an exact size, for layout and responsive tests. */
    protected void resizeWindow(int width, int height) {
        driver().manage().window().setSize(new Dimension(width, height));
    }

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DRIVER.set(DriverFactory.create(Config.browser(), Config.headless(), Config.gridUrl()));
    }

    /**
     * Takes a screenshot if the test failed (or was skipped because its setup failed),
     * then closes the browser. The screenshot must happen here, before quit().
     */
    @AfterMethod(alwaysRun = true)
    public void quitBrowser(ITestResult result) {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP) {
                Screenshots.capture(driver, result);
            }
        } finally {
            driver.quit();
            DRIVER.remove();
        }
    }
}
