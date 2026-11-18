package com.nevena.ecommerce.pages;

import java.net.URI;
import java.util.Objects;

import com.nevena.ecommerce.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Shared driver, wait and helpers for every page object. */
public abstract class BasePage {
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
}
