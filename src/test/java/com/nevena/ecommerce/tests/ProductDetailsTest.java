package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.InventoryPage.Product;
import com.nevena.ecommerce.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProductDetailsTest extends ShopBaseTest {

    @DataProvider(name = "products")
    public Object[][] products() {
        return new Object[][] {
                {Products.BACKPACK},
                {Products.BIKE_LIGHT},
                {Products.BOLT_TSHIRT}
        };
    }

    @Test(groups = "regression",
            dataProvider = "products",
            description = "Clicking a product opens a detail page with the same name, description and price as the list")
    public void detailPageMatchesListing(String productName) {
        Product listed = inventory().getProducts().stream()
                .filter(p -> p.name().equals(productName)).findFirst().orElseThrow();

        ProductPage details = inventory().openProduct(productName);

        Assert.assertEquals(details.currentPath(), ProductPage.PATH);
        Assert.assertEquals(details.getName(), listed.name());
        Assert.assertEquals(details.getDescription(), listed.description());
        Assert.assertEquals(details.getPrice(), listed.price());
    }

    @Test(groups = "regression",
            description = "Add to cart and Remove on the detail page update the cart badge")
    public void addAndRemoveOnDetailPageUpdateBadge() {
        ProductPage details = inventory().openProduct(Products.BACKPACK);

        details.addToCart();
        details.waitForCartBadgeCount(1);
        Assert.assertEquals(details.getCartBadgeCount(), 1);

        details.removeFromCart();
        details.waitForCartBadgeCount(0);
        Assert.assertEquals(details.getCartBadgeCount(), 0);
    }

    @Test(groups = "regression",
            description = "Back to products returns to the inventory and keeps the item added on the detail page")
    public void backToProductsReturnsToInventory() {
        ProductPage details = inventory().openProduct(Products.BIKE_LIGHT);
        details.addToCart();

        InventoryPage back = details.backToProducts();

        Assert.assertEquals(back.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(back.getCartBadgeCount(), 1);
    }
}
