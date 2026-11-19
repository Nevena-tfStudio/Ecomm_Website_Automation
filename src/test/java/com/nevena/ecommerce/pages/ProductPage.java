package com.nevena.ecommerce.pages;

import io.qameta.allure.Step;
import java.math.BigDecimal;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The detail page of a single product. */
public class ProductPage extends BasePage {
    public static final String PATH = "/inventory-item.html";
    private static final By NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By DESCRIPTION = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ADD_TO_CART = By.cssSelector("[data-test='add-to-cart']");
    private static final By REMOVE = By.cssSelector("[data-test='remove']");
    private static final By BACK_TO_PRODUCTS = By.cssSelector("[data-test='back-to-products']");

    /** Waits until the product details have loaded. */
    public ProductPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains(PATH));
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAME));
    }

    public String getName() {
        return driver.findElement(NAME).getText();
    }

    public String getDescription() {
        return driver.findElement(DESCRIPTION).getText();
    }

    public BigDecimal getPrice() {
        return Money.parse(driver.findElement(PRICE).getText());
    }

    /** Clicks "Add to cart" and waits until the button turns into "Remove". */
    @Step("Add the product to the cart from its details page")
    public void addToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(REMOVE));
    }

    /** Clicks "Remove" and waits until the button turns back into "Add to cart". */
    @Step("Remove the product from the cart on its details page")
    public void removeFromCart() {
        wait.until(ExpectedConditions.elementToBeClickable(REMOVE)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART));
    }

    @Step("Go back to the product list")
    public InventoryPage backToProducts() {
        driver.findElement(BACK_TO_PRODUCTS).click();
        return new InventoryPage(driver);
    }
}
