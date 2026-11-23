package com.nevena.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Response of POST /booking: the new id plus the stored booking. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatedBooking(int bookingid, Booking booking) {
}
