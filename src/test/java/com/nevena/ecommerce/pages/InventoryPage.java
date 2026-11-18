package com.nevena.ecommerce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The product list shown after a successful login. */
public class InventoryPage extends BasePage {
    public static final String PATH = "/inventory.html";
    private static final By TITLE = By.cssSelector("[data-test='title']");

    /** Waits until the inventory page has actually loaded. */
    public InventoryPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    public String getTitle() {
        return driver.findElement(TITLE).getText();
    }
}
