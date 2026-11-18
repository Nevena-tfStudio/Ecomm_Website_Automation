package com.nevena.ecommerce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Confirmation page shown after an order is placed. */
public class CheckoutCompletePage extends BasePage {
    public static final String PATH = "/checkout-complete.html";
    private static final By HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By BACK_HOME = By.cssSelector("[data-test='back-to-products']");

    /** Waits until the confirmation header is displayed. */
    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER));
    }

    public String getHeaderText() {
        return driver.findElement(HEADER).getText();
    }

    public InventoryPage backHome() {
        driver.findElement(BACK_HOME).click();
        return new InventoryPage(driver);
    }
}
