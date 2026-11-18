package com.nevena.ecommerce.support;

import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/** Creates the browser chosen by the "browser" setting (chrome or firefox). */
public final class DriverFactory {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private DriverFactory() {
    }

    public static WebDriver create(String browser, boolean headless) {
        return switch (browser.toLowerCase(Locale.ROOT)) {
            case "chrome" -> chrome(headless);
            case "firefox" -> firefox(headless);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Use chrome or firefox.");
        };
    }

    private static WebDriver chrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        // Turn off Chrome's password manager: after logging in with SauceDemo's public password,
        // its "password found in a data breach" popup can appear and swallow the test's clicks.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        if (headless) {
            options.addArguments("--headless=new", "--window-size=" + WIDTH + "," + HEIGHT);
        }
        return new ChromeDriver(options);
    }

    private static WebDriver firefox(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("signon.rememberSignons", false); // no "save password?" prompt
        if (headless) {
            options.addArguments("-headless", "--width=" + WIDTH, "--height=" + HEIGHT);
        }
        return new FirefoxDriver(options);
    }
}
