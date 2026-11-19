package com.nevena.ecommerce.tests;

import java.util.List;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import com.nevena.ecommerce.support.Accessibility;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Accessibility checks with axe-core on the login, inventory and cart pages.
 *
 * What axe reports on SauceDemo (checked 2026-11-19, Chrome 157, axe-core via axe-selenium 4.14.0):
 * - WCAG 2 A/AA rules: no violations on any of the three pages. The regression tests below pin that, so a
 *   new WCAG A/AA violation (any rule, any impact) fails the build. This is stricter than "no critical
 *   violations" and is still stable, because the baseline is zero and does not depend on a list of known ids.
 * - axe "best-practice" rules (not WCAG failures, but real gaps): the login page has no h1 and its content is
 *   not inside a landmark region; the inventory and cart pages have no h1. Those are the known-defect tests.
 *
 * KNOWN DEFECT (found 2026-11-19): the best-practice violations above. The known-defect tests state the CORRECT
 * behaviour (no best-practice violations) and fail until the site is fixed, so they are in the "known-defect"
 * group. Run them with: mvn test -DsuiteXml=src/test/resources/known-defects.xml
 *
 * Every scan attaches the violations as JSON to the Allure report.
 */
public class AccessibilityTest extends BaseTest {

    /** Opens the login page. */
    private LoginPage openLogin() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.open();
        return loginPage;
    }

    /** Opens the site and logs in with the configured user. */
    private InventoryPage loginToInventory() {
        return openLogin().loginAs(Config.username(), Config.password());
    }

    @Test(groups = "regression",
            description = "The login page has no WCAG 2 A/AA accessibility violations")
    public void loginPageHasNoWcagViolations() {
        openLogin();
        assertNoViolations(Accessibility.findViolations(driver(), "login page"), "login page");
    }

    @Test(groups = "regression",
            description = "The inventory page has no WCAG 2 A/AA accessibility violations")
    public void inventoryPageHasNoWcagViolations() {
        loginToInventory();
        assertNoViolations(Accessibility.findViolations(driver(), "inventory page"), "inventory page");
    }

    @Test(groups = "regression",
            description = "The cart page has no WCAG 2 A/AA accessibility violations")
    public void cartPageHasNoWcagViolations() {
        loginToInventory().openCart();
        assertNoViolations(Accessibility.findViolations(driver(), "cart page"), "cart page");
    }

    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: the login page has no axe best-practice violations")
    public void loginPageHasNoBestPracticeViolations() {
        openLogin();
        assertNoViolations(Accessibility.findBestPracticeViolations(driver(), "login page"), "login page");
    }

    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: the inventory page has no axe best-practice violations")
    public void inventoryPageHasNoBestPracticeViolations() {
        loginToInventory();
        assertNoViolations(Accessibility.findBestPracticeViolations(driver(), "inventory page"), "inventory page");
    }

    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: the cart page has no axe best-practice violations")
    public void cartPageHasNoBestPracticeViolations() {
        loginToInventory().openCart();
        assertNoViolations(Accessibility.findBestPracticeViolations(driver(), "cart page"), "cart page");
    }

    private static void assertNoViolations(List<String> violations, String page) {
        Assert.assertTrue(violations.isEmpty(), "Accessibility violations on the " + page + ": " + violations);
    }
}
