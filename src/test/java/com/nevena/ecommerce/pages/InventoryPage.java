package com.nevena.ecommerce.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import com.nevena.ecommerce.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The product list shown after a successful login. */
public class InventoryPage extends BasePage {
    public static final String PATH = "/inventory.html";
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESCRIPTION = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_BUTTON = By.cssSelector("button");
    private static final By SORT_DROPDOWN = By.cssSelector("[data-test='product-sort-container']");
    private static final By ACTIVE_SORT = By.cssSelector("[data-test='active-option']");
    // How long showsAddToCart() waits before deciding the button did not change.
    private static final Duration BUTTON_CHANGE_WAIT = Duration.ofSeconds(2);

    /** The options of the sort dropdown, with the text SauceDemo shows for each. */
    public enum SortOption {
        NAME_A_TO_Z("Name (A to Z)"),
        NAME_Z_TO_A("Name (Z to A)"),
        PRICE_LOW_TO_HIGH("Price (low to high)"),
        PRICE_HIGH_TO_LOW("Price (high to low)");

        private final String label;

        SortOption(String label) {
            this.label = label;
        }
    }

    /** One product row as shown in the list; buttonText is "Add to cart" or "Remove". */
    public record Product(String name, String description, BigDecimal price, String buttonText) {
    }

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

    /** Chooses an option in the sort dropdown and waits until the page shows it as active. */
    public void sortBy(SortOption option) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(SORT_DROPDOWN)))
                .selectByVisibleText(option.label);
        wait.until(ExpectedConditions.textToBe(ACTIVE_SORT, option.label));
    }

    /** All products in display order. */
    public List<Product> getProducts() {
        return new WebDriverWait(driver, Config.timeout())
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    List<WebElement> items = d.findElements(ITEM);
                    if (items.isEmpty()) {
                        return null;
                    }
                    return items.stream().map(item -> new Product(
                            item.findElement(ITEM_NAME).getText(),
                            item.findElement(ITEM_DESCRIPTION).getText(),
                            Money.parse(item.findElement(ITEM_PRICE).getText()),
                            item.findElement(ITEM_BUTTON).getText())).toList();
                });
    }

    /**
     * True if the product's button shows "Add to cart". Waits a couple of seconds for it to appear,
     * so a button that never changes is reported as false rather than an exception.
     */
    public boolean showsAddToCart(String productName) {
        try {
            new WebDriverWait(driver, BUTTON_CHANGE_WAIT)
                    .until(ExpectedConditions.visibilityOfElementLocated(buttonLocator("add-to-cart", productName)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Clicks the product's name to open its detail page. */
    public ProductPage openProduct(String productName) {
        driver.findElement(By.xpath("//*[@data-test='inventory-item-name'][normalize-space()='"
                + productName + "']")).click();
        return new ProductPage(driver);
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
