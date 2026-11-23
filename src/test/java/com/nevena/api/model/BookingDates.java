package com.nevena.api.model;

/** Check-in and check-out as the API sends them: "yyyy-MM-dd" text. */
public record BookingDates(String checkin, String checkout) {
}
