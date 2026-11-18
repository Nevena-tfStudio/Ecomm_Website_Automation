package com.nevena.ecommerce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Checkout step one: customer information. */
public class CheckoutInformationPage extends BasePage {
    public static final String PATH = "/checkout-step-one.html";
    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By CANCEL = By.cssSelector("[data-test='cancel']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    /** Waits until the information form is displayed. */
    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));
    }

    /** Fills the form and clicks Continue without expecting success (for negative tests). */
    public void submitInformation(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        driver.findElement(CONTINUE).click();
    }

    /** Fills the form with valid data and moves on to the overview. */
    public CheckoutOverviewPage continueWith(String firstName, String lastName, String postalCode) {
        submitInformation(firstName, lastName, postalCode);
        return new CheckoutOverviewPage(driver);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR)).getText();
    }

    public CartPage cancel() {
        driver.findElement(CANCEL).click();
        return new CartPage(driver);
    }

    private void type(By locator, String text) {
        var input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        input.clear();
        input.sendKeys(text);
    }
}
