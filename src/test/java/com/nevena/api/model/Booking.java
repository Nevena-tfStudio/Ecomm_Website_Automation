package com.nevena.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/** A booking, as sent to and returned by /booking. Null fields are left out of the JSON. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Booking(String firstname,
                      String lastname,
                      Integer totalprice,
                      Boolean depositpaid,
                      BookingDates bookingdates,
                      String additionalneeds) {
}
