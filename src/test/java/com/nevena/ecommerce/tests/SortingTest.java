package com.nevena.ecommerce.tests;

import java.util.Comparator;
import java.util.List;

import com.nevena.ecommerce.pages.InventoryPage.Product;
import com.nevena.ecommerce.pages.InventoryPage.SortOption;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SortingTest extends ShopBaseTest {
    private static final int PRODUCT_COUNT = 6;
    private static final String ADD_TO_CART_TEXT = "Add to cart";

    private static final Comparator<Product> BY_NAME = Comparator.comparing(Product::name);
    private static final Comparator<Product> BY_PRICE = Comparator.comparing(Product::price);

    @Test(groups = "regression",
            description = "The inventory lists 6 products, each with a name, a price and an Add to cart button")
    public void inventoryShowsSixCompleteProducts() {
        List<Product> products = inventory().getProducts();

        Assert.assertEquals(products.size(), PRODUCT_COUNT);
        for (Product product : products) {
            Assert.assertFalse(product.name().isBlank(), "A product has no name");
            Assert.assertTrue(product.price().signum() > 0, product.name() + " has no price");
            Assert.assertEquals(product.buttonText(), ADD_TO_CART_TEXT, "Button of " + product.name());
        }
    }

    @DataProvider(name = "sortOptions")
    public Object[][] sortOptions() {
        return new Object[][] {
                {SortOption.NAME_A_TO_Z, BY_NAME},
                {SortOption.NAME_Z_TO_A, BY_NAME.reversed()},
                {SortOption.PRICE_LOW_TO_HIGH, BY_PRICE},
                {SortOption.PRICE_HIGH_TO_LOW, BY_PRICE.reversed()}
        };
    }

    @Test(groups = "regression",
            dataProvider = "sortOptions",
            description = "Each sort option orders the products by name or price, ascending or descending")
    public void sortOptionOrdersProducts(SortOption option, Comparator<Product> expectedOrder) {
        List<String> namesBefore = inventory().getProducts().stream().map(Product::name).sorted().toList();

        inventory().sortBy(option);
        List<Product> shown = inventory().getProducts();

        // Each product must come before (or tie with) the next one. Ties are fine: two products cost $15.99.
        for (int i = 1; i < shown.size(); i++) {
            Product previous = shown.get(i - 1);
            Product current = shown.get(i);
            Assert.assertTrue(expectedOrder.compare(previous, current) <= 0,
                    option + ": \"" + previous.name() + "\" (" + previous.price() + ") is shown before \""
                            + current.name() + "\" (" + current.price() + ")");
        }
        Assert.assertEquals(shown.stream().map(Product::name).sorted().toList(), namesBefore,
                "Sorting must not add or drop products");
    }
}
