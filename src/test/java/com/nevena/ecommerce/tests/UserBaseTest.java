package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;

/**
 * Base for tests that log in as a specific user (the special SauceDemo users) instead of the default one.
 * Each test calls openLogin() or loginAs() itself, so nothing is kept in fields (tests run in parallel).
 */
public abstract class UserBaseTest extends BaseTest {
    /** The login page of this thread's browser, already opened. */
    protected LoginPage openLogin() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.open();
        return loginPage;
    }

    /** Opens the site and logs in as the given user (all special users share the configured password). */
    protected InventoryPage loginAs(String username) {
        return openLogin().loginAs(username, Config.password());
    }
}
