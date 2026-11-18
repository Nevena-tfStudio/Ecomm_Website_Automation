package com.nevena.ecommerce.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The shopping cart. */
public class CartPage extends BasePage {
    public static final String PATH = "/cart.html";
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CONTINUE_SHOPPING = By.cssSelector("[data-test='continue-shopping']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");
    // How long tryCheckout() waits for navigation before deciding checkout was blocked.
    private static final Duration BLOCKED_CHECKOUT_WAIT = Duration.ofSeconds(3);

    /** Waits until the cart page has loaded. */
    public CartPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    /** Item names in the cart, in display order. */
    public List<String> getItemNames() {
        return ItemList.names(driver);
    }

    /** Item name -> price for every row in the cart. */
    public Map<String, BigDecimal> getItemPrices() {
        return ItemList.prices(driver);
    }

    /** Removes the item and waits until its row has left the list. */
    public void removeItem(String productName) {
        driver.findElement(InventoryPage.buttonLocator("remove", productName)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                InventoryPage.buttonLocator("remove", productName)));
    }

    public InventoryPage continueShopping() {
        driver.findElement(CONTINUE_SHOPPING).click();
        return new InventoryPage(driver);
    }

    public CheckoutInformationPage checkout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT)).click();
        return new CheckoutInformationPage(driver);
    }

    /**
     * Clicks Checkout without assuming it works (for negative tests).
     * Returns true if the checkout information step opened, false if checkout was blocked
     * (button missing or hidden, or the page stayed put for a few seconds).
     */
    public boolean tryCheckout() {
        List<WebElement> buttons = driver.findElements(CHECKOUT);
        if (buttons.isEmpty() || !buttons.get(0).isDisplayed()) {
            return false;
        }
        buttons.get(0).click();
        try {
            new WebDriverWait(driver, BLOCKED_CHECKOUT_WAIT)
                    .until(ExpectedConditions.urlContains(CheckoutInformationPage.PATH));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
