package com.nevena.ecommerce.tests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.nevena.ecommerce.data.Customer;
import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.CheckoutInformationPage;
import com.nevena.ecommerce.pages.CheckoutInformationPage.FieldValues;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.InventoryPage.Product;
import com.nevena.ecommerce.pages.InventoryPage.SortOption;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Bugs of problem_user. Every test here is in "known-defect" and fails until the site is fixed. */
public class ProblemUserTest extends UserBaseTest {
    /*
     * KNOWN DEFECT (found 2026-11-19): for problem_user every product shows the same picture
     * (the "sl-404" placeholder) instead of its own photo.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: problem_user sees a different picture for each product")
    public void productImagesAreDistinct() {
        List<String> sources = loginAs(Users.PROBLEM).getImageSources();

        Assert.assertEquals(new HashSet<>(sources).size(), sources.size(),
                "Every product should have its own picture, but the pictures are: " + sources);
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): for problem_user the sort dropdown does nothing; choosing
     * "Name (Z to A)" leaves the products in A-to-Z order.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: sorting by name Z to A reorders the products for problem_user")
    public void sortingByNameReordersProducts() {
        InventoryPage inventory = loginAs(Users.PROBLEM);

        inventory.trySortBy(SortOption.NAME_Z_TO_A);

        List<String> names = inventory.getProducts().stream().map(Product::name).toList();
        List<String> expected = names.stream().sorted(Comparator.reverseOrder()).toList();
        Assert.assertEquals(names, expected, "Products should be listed Z to A");
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): for problem_user "Add to cart" does nothing for some products
     * (Bolt T-Shirt, Fleece Jacket and the Red T-Shirt): the button stays "Add to cart" and the badge does not count them.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: Add to cart works for every product for problem_user")
    public void addToCartWorksForEveryProduct() {
        InventoryPage inventory = loginAs(Users.PROBLEM);
        List<String> brokenProducts = new ArrayList<>();

        for (Product product : inventory.getProducts()) {
            if (!inventory.tryAddToCart(product.name())) {
                brokenProducts.add(product.name());
            }
        }

        Assert.assertTrue(brokenProducts.isEmpty(), "Add to cart did nothing for: " + brokenProducts);
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): on the checkout information step problem_user's typing in the
     * Last Name field does not stay there: the field stays empty and the text lands in the First Name field
     * (so Continue shows "Last Name is required").
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: checkout fields keep the typed values for problem_user")
    public void checkoutFieldsKeepTypedValues() {
        InventoryPage inventory = loginAs(Users.PROBLEM);
        inventory.addToCart(Products.BACKPACK);
        CheckoutInformationPage info = inventory.openCart().checkout();

        info.fillInformation(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);

        Assert.assertEquals(info.getFieldValues(),
                new FieldValues(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE));
    }
}
