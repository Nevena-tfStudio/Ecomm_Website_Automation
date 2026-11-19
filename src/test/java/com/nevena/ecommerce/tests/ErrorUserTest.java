package com.nevena.ecommerce.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nevena.ecommerce.data.Customer;
import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.CheckoutOverviewPage;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.InventoryPage.Product;
import com.nevena.ecommerce.pages.InventoryPage.SortOption;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Bugs of error_user. Every test here is in "known-defect" and fails until the site is fixed. */
public class ErrorUserTest extends UserBaseTest {
    /*
     * KNOWN DEFECT (found 2026-11-19): for error_user choosing any sort option raises a JavaScript
     * alert "Sorting is broken! This error has been reported to Backtrace." and the order does not change.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: sorting does not raise an error alert for error_user")
    public void sortingDoesNotRaiseAlert() {
        InventoryPage inventory = loginAs(Users.ERROR);

        Optional<String> alert = inventory.sortAndGetAlert(SortOption.NAME_Z_TO_A);

        Assert.assertTrue(alert.isEmpty(), "Sorting raised an alert: " + alert.orElse(""));
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): for error_user some add/remove buttons do nothing: Add to cart
     * is dead for the Bolt T-Shirt, Fleece Jacket and Red T-Shirt, and Remove is dead for the Backpack, Bike Light
     * and Onesie (the products that could be added).
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: every product can be added to and removed from the cart for error_user")
    public void addAndRemoveWorkForEveryProduct() {
        InventoryPage inventory = loginAs(Users.ERROR);
        List<String> failures = new ArrayList<>();

        for (Product product : inventory.getProducts()) {
            String name = product.name();
            if (!inventory.tryAddToCart(name)) {
                failures.add("add: " + name);
            } else if (!inventory.tryRemoveFromCart(name)) {
                failures.add("remove: " + name);
            }
        }

        Assert.assertTrue(failures.isEmpty(), "Buttons that did nothing: " + failures);
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): for error_user the Finish button on the checkout overview does
     * nothing: the order is never placed and the confirmation page does not open.
     * (error_user's Last Name field also loses its text, like problem_user's, but that is not asserted here.)
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: Finish places the order for error_user")
    public void finishPlacesTheOrder() {
        InventoryPage inventory = loginAs(Users.ERROR);
        inventory.addToCart(Products.BACKPACK);
        CheckoutOverviewPage overview = inventory.openCart().checkout()
                .continueWith(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);

        boolean confirmed = overview.tryFinish();

        Assert.assertTrue(confirmed, "Finish should open the order confirmation, but stayed on " + overview.currentPath());
    }
}
