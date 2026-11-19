package com.nevena.ecommerce.tests;

import com.nevena.ecommerce.data.PageTexts;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Regression: SauceDemo's special users can all log in. Their bugs are in the ProblemUser/ErrorUser/... classes. */
public class SpecialUsersTest extends UserBaseTest {
    @DataProvider(name = "specialUsers")
    public Object[][] specialUsers() {
        return new Object[][] {
                {Users.PROBLEM},
                {Users.ERROR},
                {Users.PERFORMANCE_GLITCH},
                {Users.VISUAL}
        };
    }

    @Test(groups = "regression",
            dataProvider = "specialUsers",
            description = "Every special user can log in and reach the inventory")
    public void specialUserReachesInventory(String username) {
        InventoryPage inventory = loginAs(username);

        Assert.assertEquals(inventory.currentPath(), InventoryPage.PATH);
        Assert.assertEquals(inventory.getTitle(), PageTexts.INVENTORY_TITLE);
    }
}
