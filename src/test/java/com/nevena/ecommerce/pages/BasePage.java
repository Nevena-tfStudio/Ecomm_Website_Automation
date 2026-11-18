package com.nevena.ecommerce.pages;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import com.nevena.ecommerce.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Shared driver, wait and helpers for every page object. */
public abstract class BasePage {
    // The cart badge is in the header, which every page after login shows.
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.wait = new WebDriverWait(driver, Config.timeout());
    }

    /** The path part of the current URL, e.g. "/inventory.html". */
    public String currentPath() {
        return URI.create(driver.getCurrentUrl()).getPath();
    }

    /** Number shown on the cart badge; 0 when the badge is not displayed. */
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText().trim());
    }

    /** Waits until the badge shows the expected count (0 means the badge is gone). */
    public void waitForCartBadgeCount(int expected) {
        if (expected == 0) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(CART_BADGE));
        } else {
            wait.until(ExpectedConditions.textToBe(CART_BADGE, String.valueOf(expected)));
        }
    }
}
