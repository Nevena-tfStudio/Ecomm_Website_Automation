package com.nevena.ecommerce.pages; // Places this class with the other page objects.

import com.nevena.ecommerce.config.Config; // Supplies the site URL from config.properties.
import org.openqa.selenium.By; // Describes how Selenium locates a page element.
import org.openqa.selenium.Rectangle; // Position and size of an element on the page.
import org.openqa.selenium.WebDriver; // Provides browser control operations.
import org.openqa.selenium.WebElement; // Represents an element such as an input or button.
import org.openqa.selenium.support.ui.ExpectedConditions; // Supplies common wait conditions.

public class LoginPage extends BasePage { // Models the login page; driver and wait come from BasePage.
    private static final By USERNAME_FIELD = By.id("user-name"); // Stable test attribute for the username input.
    private static final By PASSWORD_FIELD = By.id("password"); // Stable test attribute for the password input.
    private static final By LOGIN_BUTTON = By.id("login-button"); // Stable test attribute for the login button.
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']"); // Container that displays a rejected-login message.
    private static final By ERROR_BOX = By.cssSelector(".error-message-container"); // Red box around the error text (it has no data-test attribute).

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

    public ErrorLayout getErrorLayout() { // Measures where the error text sits compared with its red box.
        Rectangle text = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getRect(); // Position and size of the error text.
        Rectangle box = driver.findElement(ERROR_BOX).getRect(); // Position and size of the red box around it.
        return new ErrorLayout(text, box); // Bundles both so a test can compare them.
    }

    /** Where the error text is drawn compared with the red box that should contain it. */
    public record ErrorLayout(Rectangle text, Rectangle box) {
        private static final int TOLERANCE_PX = 1; // Allows for sub-pixel rounding in browser measurements.

        /** True when the text starts and ends inside the box vertically (it isn't cut off at the top or bottom). */
        public boolean textFitsInBox() {
            return text.getY() >= box.getY() - TOLERANCE_PX
                    && bottom(text) <= bottom(box) + TOLERANCE_PX;
        }

        /** Readable summary for assertion messages, e.g. "text y 244-304 (60px), box y 252-297 (45px)". */
        public String describe() {
            return "text y " + text.getY() + "-" + bottom(text) + " (" + text.getHeight() + "px), "
                    + "box y " + box.getY() + "-" + bottom(box) + " (" + box.getHeight() + "px)";
        }

        private static int bottom(Rectangle r) {
            return r.getY() + r.getHeight();
        }
    }
} // Ends the LoginPage class.
