package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.data.ErrorMessages;
import com.nevena.ecommerce.data.PageTexts;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    // Runs after BaseTest.startBrowser(), so the browser already exists.
    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage().open();
    }

    /** A page object for this thread's browser (LoginPage holds no state besides the driver). */
    private LoginPage loginPage() {
        return new LoginPage(driver());
    }

    @Test(groups = {"smoke", "regression"},
            description = "A valid user can log in and see the inventory")
    public void validCredentialsOpenInventory() {
        InventoryPage inventory = loginPage().loginAs(Config.username(), Config.password());

        Assert.assertEquals(inventory.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(inventory.getTitle(), PageTexts.INVENTORY_TITLE);
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
        loginPage().submitLogin(username, password);

        Assert.assertEquals(loginPage().getErrorMessage(), ErrorMessages.INVALID_CREDENTIALS);
    }

    @Test(groups = "regression",
            description = "Submitting an empty form shows a username-required error")
    public void emptyCredentialsShowUsernameError() {
        loginPage().submitLogin("", "");

        Assert.assertEquals(loginPage().getErrorMessage(), ErrorMessages.USERNAME_REQUIRED);
    }

    @Test(groups = "regression",
            description = "Submitting a username without a password shows a password-required error")
    public void missingPasswordShowsPasswordError() {
        loginPage().submitLogin(Config.username(), "");

        Assert.assertEquals(loginPage().getErrorMessage(), ErrorMessages.PASSWORD_REQUIRED);
    }

    @Test(groups = "regression",
            description = "A locked-out user is refused with the locked-out error")
    public void lockedOutUserIsRefused() {
        loginPage().submitLogin(Users.LOCKED_OUT, Config.password());

        Assert.assertEquals(loginPage().getErrorMessage(), ErrorMessages.LOCKED_OUT_USER);
        Assert.assertEquals(loginPage().currentPath(), "/");
    }

    @Test(groups = "regression",
            description = "The X button closes the error message")
    public void errorMessageCanBeClosed() {
        loginPage().submitLogin("unknown_user", "wrong_password");
        Assert.assertEquals(loginPage().getErrorMessage(), ErrorMessages.INVALID_CREDENTIALS);

        loginPage().closeError();

        Assert.assertFalse(loginPage().isErrorDisplayed(), "Error message is still shown");
    }

    @Test(groups = "regression",
            description = "Pressing Enter in the password field submits the login form")
    public void enterKeySubmitsLoginForm() {
        InventoryPage inventory = loginPage().loginWithEnterKey(Config.username(), Config.password());

        Assert.assertEquals(inventory.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(inventory.getTitle(), PageTexts.INVENTORY_TITLE);
    }
}
