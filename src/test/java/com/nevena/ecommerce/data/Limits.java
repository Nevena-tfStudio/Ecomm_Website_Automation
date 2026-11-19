package com.nevena.ecommerce.data;

import java.time.Duration;

/** Time limits that tests hold the site to (not to be confused with the page-object wait timeout). */
public final class Limits {
    /** A login should reach the inventory at least this fast. */
    public static final Duration MAX_LOGIN_TIME = Duration.ofSeconds(2);

    private Limits() {
    }
}
