package com.nevena.ecommerce.tests;

import java.math.BigDecimal;
import java.util.List;

import com.nevena.ecommerce.data.PageTexts;
import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.InventoryPage;
import com.nevena.ecommerce.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Bugs of visual_user. Every test here is in "known-defect" and fails until the site is fixed. */
public class VisualUserTest extends UserBaseTest {
    /*
     * KNOWN DEFECT (found 2026-11-19): for visual_user the Sauce Labs Backpack shows the "sl-404"
     * placeholder picture instead of its own photo.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: visual_user sees a real picture for every product (no placeholder)")
    public void noProductShowsThePlaceholderImage() {
        List<String> sources = loginAs(Users.VISUAL).getImageSources();

        List<String> placeholders = sources.stream().filter(src -> src.contains(PageTexts.PLACEHOLDER_IMAGE)).toList();
        Assert.assertTrue(placeholders.isEmpty(),
                placeholders.size() + " product(s) show the placeholder picture; all pictures: " + sources);
    }

    /*
     * KNOWN DEFECT (found 2026-11-19): for visual_user the prices on the inventory page are wrong and
     * change on every page load (e.g. the Backpack shows $7.32 or $79.4 in the list), while its detail page
     * shows the real $29.99.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: the price in the list matches the product's detail page for visual_user")
    public void listPriceMatchesDetailPrice() {
        InventoryPage inventory = loginAs(Users.VISUAL);
        BigDecimal listPrice = inventory.getPrice(Products.BACKPACK);

        ProductPage details = inventory.openProduct(Products.BACKPACK);

        Assert.assertEquals(listPrice, details.getPrice(), "List price vs detail price of " + Products.BACKPACK);
    }
}
