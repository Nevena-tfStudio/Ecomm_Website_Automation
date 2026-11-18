package com.nevena.ecommerce.tests;

import java.util.Map;

import com.nevena.ecommerce.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Starts a fresh browser before each test and closes it afterwards.
 * Every test class extends this. alwaysRun = true keeps setup running
 * when a suite selects tests by group (smoke/regression).
 */
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        // Turn off Chrome's password manager: after logging in with SauceDemo's public password,
        // its "password found in a data breach" popup can appear and swallow the test's clicks.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        if (Config.headless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
    }

    @AfterMethod(alwaysRun = true)
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
