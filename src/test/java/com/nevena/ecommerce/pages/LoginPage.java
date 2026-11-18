package com.nevena.ecommerce.pages; // Places this class with the other page objects.

import com.nevena.ecommerce.config.Config; // Supplies the site URL from config.properties.
import org.openqa.selenium.By; // Describes how Selenium locates a page element.
import org.openqa.selenium.WebDriver; // Provides browser control operations.
import org.openqa.selenium.WebElement; // Represents an element such as an input or button.
import org.openqa.selenium.support.ui.ExpectedConditions; // Supplies common wait conditions.

public class LoginPage extends BasePage { // Models the login page; driver and wait come from BasePage.
    private static final By USERNAME_FIELD = By.id("user-name"); // Stable test attribute for the username input.
    private static final By PASSWORD_FIELD = By.id("password"); // Stable test attribute for the password input.
    private static final By LOGIN_BUTTON = By.id("login-button"); // Stable test attribute for the login button.
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']"); // Container that displays a rejected-login message.

    public LoginPage(WebDriver driver) { // Receives the browser from the test instead of creating a second browser.
        super(driver); // BasePage checks the driver and creates the shared wait.
    }

    public void open() { // Navigates to the login page.
        driver.get(Config.baseUrl()); // Opens the site URL configured in config.properties.
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_FIELD)); // Confirms the login form is ready before a test uses it.
    }

    public InventoryPage loginAs(String username, String password) { // Logs in with valid credentials and returns the page that follows.
        submitLogin(username, password); // Fills in and submits the form.
        return new InventoryPage(driver); // InventoryPage waits until it has loaded.
    }

    public void submitLogin(String username, String password) { // Submits the form without expecting success (for negative tests).
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_FIELD)); // Waits for the username box to appear.
        usernameInput.clear(); // Removes any value left in the field.
        usernameInput.sendKeys(username); // Types the username supplied by the test.

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD)); // Waits for the password box to appear.
        passwordInput.clear(); // Removes any value left in the field.
        passwordInput.sendKeys(password); // Types the password supplied by the test.

        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click(); // Clicks only after Selenium sees an enabled, clickable button.
    }

    public String getErrorMessage() { // Reads the error shown after a rejected login.
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText(); // Waits for and returns the displayed message.
    }
} // Ends the LoginPage class.
