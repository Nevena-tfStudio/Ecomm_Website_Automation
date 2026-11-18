package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.data.ErrorMessages;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    private LoginPage loginPage;

    // Runs after BaseTest.startBrowser(), so the driver already exists.
    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open();
    }

    @Test(groups = {"smoke", "regression"},
            description = "A valid user can log in and see the inventory")
    public void validCredentialsOpenInventory() {
        InventoryPage inventory = loginPage.loginAs(Config.username(), Config.password());

        Assert.assertEquals(inventory.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(inventory.getTitle(), "Products");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
                {"unknown_user", Config.password()},
                {Config.username(), "wrong_password"},
                {"unknown_user", "wrong_password"}
        };
    }

    @Test(groups = "regression",
            dataProvider = "invalidCredentials",
            description = "Invalid credentials are rejected")
    public void invalidCredentialsAreRejected(String username, String password) {
        loginPage.submitLogin(username, password);

        Assert.assertEquals(loginPage.getErrorMessage(), ErrorMessages.INVALID_CREDENTIALS);
    }

    @Test(groups = "regression",
            description = "Submitting an empty form shows a username-required error")
    public void emptyCredentialsShowUsernameError() {
        loginPage.submitLogin("", "");

        Assert.assertEquals(loginPage.getErrorMessage(), ErrorMessages.USERNAME_REQUIRED);
    }

    @Test(groups = "regression",
            description = "Submitting a username without a password shows a password-required error")
    public void missingPasswordShowsPasswordError() {
        loginPage.submitLogin(Config.username(), "");

        Assert.assertEquals(loginPage.getErrorMessage(), ErrorMessages.PASSWORD_REQUIRED);
    }
}
