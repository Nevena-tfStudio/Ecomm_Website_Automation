package com.nevena.ecommerce.support;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates the browser chosen by the "browser" setting (chrome or firefox): on this machine, or on a
 * Selenium Grid when a Grid URL is given. The browser options are the same either way.
 */
public final class DriverFactory {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private DriverFactory() {
    }

    public static WebDriver create(String browser, boolean headless, Optional<String> gridUrl) {
        return switch (browser.toLowerCase(Locale.ROOT)) {
            case "chrome" -> start(chromeOptions(headless), gridUrl, ChromeDriver::new);
            case "firefox" -> start(firefoxOptions(headless), gridUrl, FirefoxDriver::new);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Use chrome or firefox.");
        };
    }

    /** Starts the browser on the Grid if there is one, otherwise on this machine. */
    private static <O extends Capabilities> WebDriver start(O options, Optional<String> gridUrl,
                                                           Function<O, WebDriver> localDriver) {
        return gridUrl.<WebDriver>map(url -> new RemoteWebDriver(toUrl(url), options))
                .orElseGet(() -> localDriver.apply(options));
    }

    private static ChromeOptions chromeOptions(boolean headless) {
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
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("signon.rememberSignons", false); // no "save password?" prompt
        if (headless) {
            options.addArguments("-headless", "--width=" + WIDTH, "--height=" + HEIGHT);
        }
        return options;
    }

    private static URL toUrl(String gridUrl) {
        try {
            return URI.create(gridUrl).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grid.url '" + gridUrl + "'", e);
        }
    }
}
