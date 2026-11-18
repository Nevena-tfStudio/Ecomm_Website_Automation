package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.data.ErrorMessages;
import com.nevena.ecommerce.data.PageTexts;
import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.LoginPage;
import com.nevena.ecommerce.pages.Menu;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Burger menu checks.
 *
 * KNOWN DEFECT: "Reset App State" empties the cart badge, but the product buttons on the inventory
 * page keep saying "Remove" (until the page is reloaded). Found 2026-11-18 in Chrome 157.
 * The test states the CORRECT behaviour and fails until the site is fixed, so it is in the
 * "known-defect" group. Run it with: mvn test -DsuiteXml=src/test/resources/known-defects.xml
 */
public class MenuTest extends ShopBaseTest {

    @Test(groups = {"smoke", "regression"},
            description = "Logout returns to the login page")
    public void logoutReturnsToLoginPage() {
        LoginPage login = inventory().openMenu().logout();

        Assert.assertTrue(login.isLoginFormDisplayed(), "Login form is not shown after logout");
        Assert.assertEquals(login.currentPath(), "/");
    }

    @Test(groups = "regression",
            description = "After logout, opening /inventory.html directly is refused with a login-required error")
    public void inventoryIsRefusedAfterLogout() {
        LoginPage login = inventory().openMenu().logout();

        login.openDirectly(InventoryPage.PATH);

        Assert.assertEquals(login.getErrorMessage(), ErrorMessages.LOGIN_REQUIRED_FOR_INVENTORY);
        Assert.assertEquals(login.currentPath(), "/");
    }

    @Test(groups = "regression",
            description = "After logout, the browser Back button does not show the inventory")
    public void backButtonDoesNotShowInventoryAfterLogout() {
        LoginPage login = inventory().openMenu().logout();

        login.goBack();

        Assert.assertTrue(login.isLoginFormDisplayed(), "Back showed something other than the login form");
        Assert.assertNotEquals(login.currentPath(), InventoryPage.PATH);
    }

    @Test(groups = "regression",
            description = "All Items in the menu returns from the cart to the inventory")
    public void allItemsReturnsFromCartToInventory() {
        InventoryPage back = inventory().openCart().openMenu().allItems();

        Assert.assertEquals(back.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(back.getTitle(), PageTexts.INVENTORY_TITLE);
    }

    @Test(groups = "regression",
            description = "Reset App State empties the cart badge")
    public void resetAppStateEmptiesCartBadge() {
        inventory().addToCart(Products.BACKPACK);
        inventory().waitForCartBadgeCount(1);

        inventory().openMenu().resetAppState();

        inventory().waitForCartBadgeCount(0);
        Assert.assertEquals(inventory().getCartBadgeCount(), 0);
    }

    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: after Reset App State the product buttons go back to Add to cart")
    public void resetAppStateRestoresAddToCartButtons() {
        inventory().addToCart(Products.BACKPACK);
        inventory().openMenu().resetAppState();
        inventory().waitForCartBadgeCount(0);

        Assert.assertTrue(inventory().showsAddToCart(Products.BACKPACK),
                "The cart is empty, but the button of " + Products.BACKPACK + " still says Remove");
    }

    @Test(groups = "regression",
            description = "The About link points to saucelabs.com (it is not opened)")
    public void aboutLinkPointsToSauceLabs() {
        Menu menu = inventory().openMenu();

        Assert.assertTrue(menu.getAboutLinkUrl().startsWith(PageTexts.ABOUT_URL_PREFIX),
                "About link was " + menu.getAboutLinkUrl());
    }
}
