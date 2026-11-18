package com.nevena.ecommerce.pages;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Checkout step two: order summary with totals. */
public class CheckoutOverviewPage extends BasePage {
    public static final String PATH = "/checkout-step-two.html";
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By SUBTOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By CANCEL = By.cssSelector("[data-test='cancel']");

    /** Waits until the overview is displayed. */
    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    public List<String> getItemNames() {
        return ItemList.names(driver);
    }

    /** Item name -> price for every row in the order. */
    public Map<String, BigDecimal> getItemPrices() {
        return ItemList.prices(driver);
    }

    /** "Item total: $x" as a number. */
    public BigDecimal getItemTotal() {
        return Money.parse(driver.findElement(SUBTOTAL).getText());
    }

    public BigDecimal getTax() {
        return Money.parse(driver.findElement(TAX).getText());
    }

    public BigDecimal getTotal() {
        return Money.parse(driver.findElement(TOTAL).getText());
    }

    public CheckoutCompletePage finish() {
        driver.findElement(FINISH).click();
        return new CheckoutCompletePage(driver);
    }

    public InventoryPage cancel() {
        driver.findElement(CANCEL).click();
        return new InventoryPage(driver);
    }
}
