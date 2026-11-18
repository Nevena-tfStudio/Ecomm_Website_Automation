package com.nevena.ecommerce.tests;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.pages.CartPage;
import com.nevena.ecommerce.pages.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends ShopBaseTest {

    @Test(groups = "regression",
            description = "Adding items raises the cart badge; removing lowers it and the badge disappears at 0")
    public void badgeFollowsAddedAndRemovedItems() {
        Assert.assertEquals(inventory.getCartBadgeCount(), 0);

        inventory.addToCart(Products.BACKPACK);
        inventory.waitForCartBadgeCount(1);
        Assert.assertEquals(inventory.getCartBadgeCount(), 1);

        inventory.addToCart(Products.BIKE_LIGHT);
        inventory.waitForCartBadgeCount(2);
        Assert.assertEquals(inventory.getCartBadgeCount(), 2);

        inventory.removeFromCart(Products.BIKE_LIGHT);
        inventory.waitForCartBadgeCount(1);
        Assert.assertEquals(inventory.getCartBadgeCount(), 1);

        inventory.removeFromCart(Products.BACKPACK);
        inventory.waitForCartBadgeCount(0);
        Assert.assertEquals(inventory.getCartBadgeCount(), 0);
    }

    @Test(groups = "regression",
            description = "The cart lists exactly the added items with the prices shown on the inventory page")
    public void cartListsAddedItemsWithInventoryPrices() {
        BigDecimal backpackPrice = inventory.getPrice(Products.BACKPACK);
        BigDecimal bikeLightPrice = inventory.getPrice(Products.BIKE_LIGHT);
        inventory.addToCart(Products.BACKPACK);
        inventory.addToCart(Products.BIKE_LIGHT);

        CartPage cart = inventory.openCart();

        Assert.assertEquals(cart.getItemPrices(),
                Map.of(Products.BACKPACK, backpackPrice, Products.BIKE_LIGHT, bikeLightPrice));
        Assert.assertEquals(cart.getItemNames().size(), 2);
    }

    @Test(groups = "regression",
            description = "Removing an item on the cart page removes its row and updates the badge")
    public void removingItemOnCartPageUpdatesListAndBadge() {
        inventory.addToCart(Products.BACKPACK);
        inventory.addToCart(Products.BIKE_LIGHT);
        CartPage cart = inventory.openCart();

        cart.removeItem(Products.BACKPACK);
        cart.waitForCartBadgeCount(1);

        Assert.assertEquals(cart.getItemNames(), List.of(Products.BIKE_LIGHT));
        Assert.assertEquals(cart.getCartBadgeCount(), 1);
    }

    @Test(groups = "regression",
            description = "Continue Shopping returns to the inventory page")
    public void continueShoppingReturnsToInventory() {
        CartPage cart = inventory.openCart();

        InventoryPage back = cart.continueShopping();

        Assert.assertEquals(back.currentPath(), InventoryPage.PATH);
    }

    @Test(groups = "regression",
            description = "Cart contents persist when going from cart to inventory and back to the cart")
    public void cartContentsPersistAcrossNavigation() {
        inventory.addToCart(Products.BACKPACK);
        inventory.addToCart(Products.BOLT_TSHIRT);

        InventoryPage back = inventory.openCart().continueShopping();
        Assert.assertEquals(back.getCartBadgeCount(), 2);

        CartPage cartAgain = back.openCart();
        Assert.assertEquals(cartAgain.getItemNames(), List.of(Products.BACKPACK, Products.BOLT_TSHIRT));
    }
}
