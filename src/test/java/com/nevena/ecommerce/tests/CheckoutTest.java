package com.nevena.ecommerce.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.nevena.ecommerce.data.Customer;
import com.nevena.ecommerce.data.ErrorMessages;
import com.nevena.ecommerce.data.PageTexts;
import com.nevena.ecommerce.data.Products;
import com.nevena.ecommerce.pages.CartPage;
import com.nevena.ecommerce.pages.CheckoutCompletePage;
import com.nevena.ecommerce.pages.CheckoutInformationPage;
import com.nevena.ecommerce.pages.CheckoutOverviewPage;
import com.nevena.ecommerce.pages.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckoutTest extends ShopBaseTest {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    @DataProvider(name = "missingInformation")
    public Object[][] missingInformation() {
        return new Object[][] {
                {"", Customer.LAST_NAME, Customer.POSTAL_CODE, ErrorMessages.FIRST_NAME_REQUIRED},
                {Customer.FIRST_NAME, "", Customer.POSTAL_CODE, ErrorMessages.LAST_NAME_REQUIRED},
                {Customer.FIRST_NAME, Customer.LAST_NAME, "", ErrorMessages.POSTAL_CODE_REQUIRED}
        };
    }

    @Test(groups = "regression",
            dataProvider = "missingInformation",
            description = "A missing required field on the information step shows the matching error")
    public void missingRequiredFieldShowsError(String first, String last, String postalCode, String expectedError) {
        inventory.addToCart(Products.BACKPACK);
        CheckoutInformationPage info = inventory.openCart().checkout();

        info.submitInformation(first, last, postalCode);

        Assert.assertEquals(info.getErrorMessage(), expectedError);
        Assert.assertEquals(info.currentPath(), CheckoutInformationPage.PATH);
    }

    @Test(groups = "regression",
            description = "Overview: item total is the sum of prices, tax is 8% rounded half-up, total is item total + tax")
    public void overviewTotalsAreCalculatedCorrectly() {
        inventory.addToCart(Products.BACKPACK);
        inventory.addToCart(Products.BIKE_LIGHT);
        inventory.addToCart(Products.BOLT_TSHIRT);

        CheckoutOverviewPage overview = inventory.openCart().checkout().continueWith(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);

        BigDecimal sumOfPrices = overview.getItemPrices().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Assert.assertEquals(overview.getItemPrices().size(), 3);
        Assert.assertEquals(overview.getItemTotal(), sumOfPrices);

        BigDecimal expectedTax = overview.getItemTotal().multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        Assert.assertEquals(overview.getTax(), expectedTax);
        Assert.assertEquals(overview.getTotal(), overview.getItemTotal().add(overview.getTax()));
    }

    @Test(groups = {"smoke", "regression"},
            description = "A user can buy an item end to end and the cart is empty afterwards")
    public void fullPurchaseCompletesAndEmptiesCart() {
        inventory.addToCart(Products.BACKPACK);

        CartPage cart = inventory.openCart();
        Assert.assertEquals(cart.getItemNames(), List.of(Products.BACKPACK));
        CheckoutOverviewPage overview = cart.checkout().continueWith(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);
        Assert.assertEquals(overview.getItemNames(), List.of(Products.BACKPACK));
        CheckoutCompletePage complete = overview.finish();

        Assert.assertEquals(complete.getHeaderText(), PageTexts.ORDER_CONFIRMATION);
        InventoryPage home = complete.backHome();
        Assert.assertEquals(home.getCartBadgeCount(), 0);
    }

    @Test(groups = "regression",
            description = "Cancel on the information step returns to the cart")
    public void cancelOnInformationStepReturnsToCart() {
        inventory.addToCart(Products.BACKPACK);
        CheckoutInformationPage info = inventory.openCart().checkout();

        CartPage cart = info.cancel();

        Assert.assertEquals(cart.currentPath(), CartPage.PATH);
        Assert.assertEquals(cart.getItemNames(), List.of(Products.BACKPACK));
    }

    @Test(groups = "regression",
            description = "Cancel on the overview step returns to the inventory")
    public void cancelOnOverviewReturnsToInventory() {
        inventory.addToCart(Products.BACKPACK);
        CheckoutOverviewPage overview = inventory.openCart().checkout().continueWith(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);

        InventoryPage back = overview.cancel();

        Assert.assertEquals(back.currentPath(), InventoryPage.PATH);
    }

    @Test(groups = "regression",
            description = "LIKELY DEFECT (documents current behaviour): checkout with an empty cart is allowed "
                    + "and reaches an overview with no items and a $0.00 total")
    public void likelyDefectCheckoutIsAllowedWithEmptyCart() {
        CartPage cart = inventory.openCart();
        Assert.assertTrue(cart.getItemNames().isEmpty());

        CheckoutOverviewPage overview = cart.checkout().continueWith(Customer.FIRST_NAME, Customer.LAST_NAME, Customer.POSTAL_CODE);

        Assert.assertTrue(overview.getItemNames().isEmpty());
        Assert.assertEquals(overview.getTotal().compareTo(BigDecimal.ZERO), 0);
    }
}
