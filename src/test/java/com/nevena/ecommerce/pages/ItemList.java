package com.nevena.ecommerce.pages;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Reads the item rows that the cart and the checkout overview display the same way. */
final class ItemList {
    private static final By ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");

    private ItemList() {
    }

    /** Item names in display order. */
    static List<String> names(WebDriver driver) {
        return driver.findElements(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    /** Item name -> price for every row, in display order. */
    static Map<String, BigDecimal> prices(WebDriver driver) {
        Map<String, BigDecimal> prices = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(ITEM)) {
            prices.put(row.findElement(ITEM_NAME).getText(),
                    Money.parse(row.findElement(ITEM_PRICE).getText()));
        }
        return prices;
    }
}
