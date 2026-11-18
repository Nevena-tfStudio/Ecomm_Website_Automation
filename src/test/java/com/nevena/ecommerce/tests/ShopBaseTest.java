package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.annotations.BeforeMethod;

/** Base for tests that start on the inventory page of a logged-in user. */
public abstract class ShopBaseTest extends BaseTest {
    // Per thread, like the browser in BaseTest (tests run in parallel).
    private static final ThreadLocal<InventoryPage> INVENTORY = new ThreadLocal<>();

    // Runs after BaseTest.startBrowser(), so the browser already exists.
    @BeforeMethod(alwaysRun = true)
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.open();
        INVENTORY.set(loginPage.loginAs(Config.username(), Config.password()));
    }

    /** The inventory page reached by this thread's login. */
    protected InventoryPage inventory() {
        return INVENTORY.get();
    }
}
