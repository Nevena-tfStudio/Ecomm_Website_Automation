package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Layout checks for the login page.
 *
 * KNOWN DEFECT: the red error box has a fixed CSS height of 45px, but the
 * invalid-credentials message wraps to 3 lines (60px) at most window sizes, so its
 * first and last lines spill outside the box (white text on white: looks cut off).
 * Found 2026-11-18 in Chrome 157 and Firefox 159.
 *
 * These tests state the CORRECT behaviour and fail until the site is fixed, so they
 * are in the "known-defect" group, which the regression and smoke suites skip.
 * Run them with: mvn test -DsuiteXml=src/test/resources/known-defects.xml
 */
public class LoginVisualTest extends BaseTest {

    @DataProvider(name = "screenSizes")
    public Object[][] screenSizes() {
        return new Object[][] {
                {"desktop", 1920, 1080},
                {"laptop", 1366, 768},
                {"mobile", 390, 844}
        };
    }

    @Test(groups = "known-defect",
            dataProvider = "screenSizes",
            description = "KNOWN DEFECT: the invalid-login error text fits inside its red error box")
    public void errorMessageFitsInsideErrorBox(String screen, int width, int height) {
        resizeWindow(width, height);
        LoginPage loginPage = new LoginPage(driver());
        loginPage.open();

        loginPage.submitLogin(Config.username(), "wrong_password");
        LoginPage.ErrorLayout layout = loginPage.getErrorLayout();

        Assert.assertTrue(layout.textFitsInBox(),
                "On " + screen + " (" + width + "x" + height + ") the error text spills outside its box: "
                        + layout.describe());
    }
}
