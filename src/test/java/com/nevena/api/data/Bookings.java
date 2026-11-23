package com.nevena.api.data;

import java.util.UUID;

import com.nevena.api.model.Booking;
import com.nevena.api.model.BookingDates;

/** Test data builders. Every booking gets a unique last name, so filters never match other people's data. */
public final class Bookings {
    public static final String FIRST_NAME = "Nevena";
    public static final String CHECKIN = "2027-03-01";
    public static final String DAY_BEFORE_CHECKIN = "2027-02-28";
    public static final String CHECKOUT = "2027-03-05";
    public static final int TOTAL_PRICE = 150;
    public static final String ADDITIONAL_NEEDS = "Breakfast";

    /** Values used by the PUT (full update) test. */
    public static final Booking UPDATED = new Booking("Updated", "Guest", 321, false,
            new BookingDates("2027-04-10", "2027-04-12"), "Late checkout");

    /** The id no booking has (ids are small, increasing integers). */
    public static final int MISSING_ID = 999_999_999;

    private Bookings() {
    }

    public static String uniqueLastName() {
        return "Api" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public static Booking valid() {
        return withLastName(uniqueLastName());
    }

    public static Booking withLastName(String lastName) {
        return new Booking(FIRST_NAME, lastName, TOTAL_PRICE, true,
                new BookingDates(CHECKIN, CHECKOUT), ADDITIONAL_NEEDS);
    }

    public static Booking withDates(String checkin, String checkout) {
        return new Booking(FIRST_NAME, uniqueLastName(), TOTAL_PRICE, true,
                new BookingDates(checkin, checkout), ADDITIONAL_NEEDS);
    }

    public static Booking withTotalPrice(int totalPrice) {
        return new Booking(FIRST_NAME, uniqueLastName(), totalPrice, true,
                new BookingDates(CHECKIN, CHECKOUT), ADDITIONAL_NEEDS);
    }
}
