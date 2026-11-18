package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.annotations.BeforeMethod;

/** Base for tests that start on the inventory page of a logged-in user. */
public abstract class ShopBaseTest extends BaseTest {
    protected InventoryPage inventory;

    // Runs after BaseTest.startBrowser(), so the driver already exists.
    @BeforeMethod(alwaysRun = true)
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        inventory = loginPage.loginAs(Config.username(), Config.password());
    }
}
