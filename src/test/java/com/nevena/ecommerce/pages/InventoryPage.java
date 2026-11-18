package com.nevena.ecommerce.pages;

import java.math.BigDecimal;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The product list shown after a successful login. */
public class InventoryPage extends BasePage {
    public static final String PATH = "/inventory.html";
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");

    /** Waits until the inventory page has actually loaded. */
    public InventoryPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    public String getTitle() {
        return driver.findElement(TITLE).getText();
    }

    /** Clicks "Add to cart" for the product and waits until its button turns into "Remove". */
    public void addToCart(String productName) {
        driver.findElement(buttonLocator("add-to-cart", productName)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(buttonLocator("remove", productName)));
    }

    /** Clicks "Remove" for the product and waits until its button turns back into "Add to cart". */
    public void removeFromCart(String productName) {
        driver.findElement(buttonLocator("remove", productName)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(buttonLocator("add-to-cart", productName)));
    }

    /** Price of a product as listed on the inventory page. */
    public BigDecimal getPrice(String productName) {
        By item = By.xpath("//*[@data-test='inventory-item'][.//*[@data-test='inventory-item-name']"
                + "[normalize-space()='" + productName + "']]//*[@data-test='inventory-item-price']");
        return Money.parse(wait.until(ExpectedConditions.visibilityOfElementLocated(item)).getText());
    }

    public CartPage openCart() {
        driver.findElement(CART_LINK).click();
        return new CartPage(driver);
    }

    /** SauceDemo derives button ids from the product name: "Sauce Labs Backpack" -> "add-to-cart-sauce-labs-backpack". */
    static By buttonLocator(String action, String productName) {
        String slug = productName.toLowerCase().replace(' ', '-');
        return By.cssSelector("[data-test='" + action + "-" + slug + "']");
    }
}
