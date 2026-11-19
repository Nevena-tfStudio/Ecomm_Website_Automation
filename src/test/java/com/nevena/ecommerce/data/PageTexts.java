package com.nevena.ecommerce.data;

/** Exact non-error texts shown by SauceDemo (titles, confirmations). Errors live in ErrorMessages. */
public final class PageTexts {
    public static final String INVENTORY_TITLE = "Products";
    public static final String ORDER_CONFIRMATION = "Thank you for your order!";

    /** The menu's About link must point at the Sauce Labs site. */
    public static final String ABOUT_URL_PREFIX = "https://saucelabs.com";

    /** Part of the file name of the "image not found" picture SauceDemo shows instead of a product photo. */
    public static final String PLACEHOLDER_IMAGE = "sl-404";

    private PageTexts() {
    }
}
