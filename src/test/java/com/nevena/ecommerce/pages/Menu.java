package com.nevena.ecommerce.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The burger menu that slides in from the left on every page after login. */
public class Menu extends BasePage {
    private static final By ALL_ITEMS = By.cssSelector("[data-test='inventory-sidebar-link']");
    private static final By ABOUT = By.cssSelector("[data-test='about-sidebar-link']");
    private static final By LOGOUT = By.cssSelector("[data-test='logout-sidebar-link']");
    private static final By RESET = By.cssSelector("[data-test='reset-sidebar-link']");
    private static final By CLOSE_BUTTON = By.id("react-burger-cross-btn");

    /** Waits until the slide-in animation has finished, so clicks do not land on a moving link. */
    public Menu(WebDriver driver) {
        super(driver);
        WebElement lastLink = wait.until(ExpectedConditions.visibilityOfElementLocated(RESET));
        int[] previousX = {Integer.MIN_VALUE};
        wait.until(d -> {
            int x = lastLink.getRect().getX();
            boolean settled = x >= 0 && x == previousX[0]; // on screen and the same as one poll ago
            previousX[0] = x;
            return settled;
        });
    }

    @Step("Log out from the menu")
    public LoginPage logout() {
        click(LOGOUT);
        return new LoginPage(driver).waitUntilDisplayed();
    }

    @Step("Choose All Items in the menu")
    public InventoryPage allItems() {
        click(ALL_ITEMS);
        return new InventoryPage(driver);
    }

    /** Clicks "Reset App State". The menu stays open afterwards. */
    @Step("Choose Reset App State in the menu")
    public void resetAppState() {
        click(RESET);
    }

    /** Where the About link points (it is not clicked, so the external site is never loaded). */
    public String getAboutLinkUrl() {
        return driver.findElement(ABOUT).getAttribute("href");
    }

    /** Closes the menu and waits until its links are hidden again. */
    @Step("Close the menu")
    public void close() {
        click(CLOSE_BUTTON);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(RESET));
    }

    /**
     * Clicks a menu link, retrying until the browser accepts the click. Even after the slide-in has
     * settled, Chrome on CI occasionally reports a link as "not interactable" for a moment.
     */
    private void click(By locator) {
        wait.ignoring(ElementNotInteractableException.class)
                .ignoring(ElementClickInterceptedException.class)
                .until(d -> {
                    d.findElement(locator).click();
                    return true;
                });
    }
}
