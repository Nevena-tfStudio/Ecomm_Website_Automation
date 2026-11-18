package com.nevena.ecommerce.pages;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses price texts such as "$29.99" or "Item total: $39.98" into exact BigDecimal values. */
public final class Money {
    private static final Pattern AMOUNT = Pattern.compile("\\$(\\d+(?:\\.\\d+)?)");

    private Money() {
    }

    public static BigDecimal parse(String text) {
        Matcher matcher = AMOUNT.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No price found in '" + text + "'");
        }
        return new BigDecimal(matcher.group(1));
    }
}
