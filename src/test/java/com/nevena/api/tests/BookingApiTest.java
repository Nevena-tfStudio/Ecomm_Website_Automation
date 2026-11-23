package com.nevena.api.tests;

import java.util.Map;

import com.nevena.api.client.AuthClient;
import com.nevena.api.client.BookingClient;
import com.nevena.api.client.TestBooking;
import com.nevena.api.data.ApiLimits;
import com.nevena.api.data.Bookings;
import com.nevena.api.model.Booking;
import com.nevena.api.model.CreatedBooking;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.hasItem;

/**
 * Booking endpoints. Every test creates its own booking in a try-with-resources block, which deletes it
 * afterwards, so tests are independent and safe to run in parallel (no state in fields).
 */
public class BookingApiTest {
    private final BookingClient bookings = new BookingClient();

    @Test(groups = {"api", "smoke"}, description = "Create a booking, then get it by id: all fields match")
    public void createdBookingCanBeRead() {
        Booking booking = Bookings.valid();
        Response created = bookings.create(booking);
        Assert.assertEquals(created.statusCode(), 200);
        CreatedBooking createdBooking = created.as(CreatedBooking.class);
        try {
            Assert.assertEquals(createdBooking.booking(), booking, "The create response echoes the booking");

            Response fetched = bookings.get(createdBooking.bookingid());
            Assert.assertEquals(fetched.statusCode(), 200);
            Assert.assertEquals(fetched.as(Booking.class), booking);
        } finally {
            TestBooking.deleteQuietly(createdBooking.bookingid());
        }
    }

    @Test(groups = "api", description = "PUT replaces every field of a booking")
    public void fullUpdateReplacesAllFields() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response updated = bookings.update(created.id(), Bookings.UPDATED, created.token());

            Assert.assertEquals(updated.statusCode(), 200);
            Assert.assertEquals(updated.as(Booking.class), Bookings.UPDATED);
            Assert.assertEquals(bookings.get(created.id()).as(Booking.class), Bookings.UPDATED,
                    "The change is stored, not just echoed");
        }
    }

    @Test(groups = "api", description = "PATCH changes only the sent fields and keeps the rest")
    public void partialUpdateChangesOnlySentFields() {
        Booking original = Bookings.valid();
        try (TestBooking created = TestBooking.create(original)) {
            Response patched = bookings.patch(created.id(),
                    Map.of("firstname", "Patched", "totalprice", 999), created.token());

            Assert.assertEquals(patched.statusCode(), 200);
            Booking expected = new Booking("Patched", original.lastname(), 999, original.depositpaid(),
                    original.bookingdates(), original.additionalneeds());
            Assert.assertEquals(patched.as(Booking.class), expected);
            Assert.assertEquals(bookings.get(created.id()).as(Booking.class), expected);
        }
    }

    @Test(groups = "api", description = "Full lifecycle: create, get, PUT, PATCH, delete, then get returns 404")
    public void fullBookingLifecycle() {
        Booking booking = Bookings.valid();
        String token = new AuthClient().validToken();
        CreatedBooking created = bookings.create(booking).then().statusCode(200).extract().as(CreatedBooking.class);
        int id = created.bookingid();
        try {
            Assert.assertEquals(bookings.get(id).as(Booking.class), booking);

            Assert.assertEquals(bookings.update(id, Bookings.UPDATED, token).statusCode(), 200);
            Assert.assertEquals(bookings.get(id).as(Booking.class), Bookings.UPDATED);

            Assert.assertEquals(bookings.patch(id, Map.of("lastname", "Patched"), token).statusCode(), 200);
            Assert.assertEquals(bookings.get(id).path("lastname"), "Patched");

            // Restful-Booker answers 201 here (see api-known-defects.xml); any 2xx means it worked
            Response deleted = bookings.delete(id, token);
            Assert.assertTrue(deleted.statusCode() / 100 == 2, "DELETE returned " + deleted.statusCode());
            Assert.assertEquals(bookings.get(id).statusCode(), 404);
        } finally {
            TestBooking.deleteQuietly(id);
        }
    }

    @Test(groups = "api", description = "PUT without a token is rejected with 403 and changes nothing")
    public void updateWithoutTokenIsForbidden() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.updateWithoutToken(created.id(), Bookings.UPDATED);

            Assert.assertEquals(response.statusCode(), 403);
            Assert.assertEquals(bookings.get(created.id()).as(Booking.class), created.booking());
        }
    }

    @Test(groups = "api", description = "PATCH without a token is rejected with 403 and changes nothing")
    public void patchWithoutTokenIsForbidden() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.patchWithoutToken(created.id(), Map.of("firstname", "Hacker"));

            Assert.assertEquals(response.statusCode(), 403);
            Assert.assertEquals(bookings.get(created.id()).as(Booking.class), created.booking());
        }
    }

    @Test(groups = "api", description = "DELETE without a token is rejected with 403 and the booking stays")
    public void deleteWithoutTokenIsForbidden() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.deleteWithoutToken(created.id());

            Assert.assertEquals(response.statusCode(), 403);
            Assert.assertEquals(bookings.get(created.id()).statusCode(), 200);
        }
    }

    @Test(groups = "api", description = "DELETE with an invalid token is rejected with 403")
    public void deleteWithInvalidTokenIsForbidden() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.delete(created.id(), "not-a-real-token");

            Assert.assertEquals(response.statusCode(), 403);
            Assert.assertEquals(bookings.get(created.id()).statusCode(), 200);
        }
    }

    @Test(groups = "api", description = "GET /booking filtered by first and last name contains the created id")
    public void listFilteredByNameContainsCreatedBooking() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.list(Map.of(
                    "firstname", created.booking().firstname(),
                    "lastname", created.booking().lastname()));

            Assert.assertEquals(response.statusCode(), 200);
            response.then().body("bookingid", hasItem(created.id()));
            Assert.assertEquals(response.jsonPath().getList("bookingid").size(), 1,
                    "The last name is unique, so exactly one booking matches");
        }
    }

    @Test(groups = "api", description = "GET /booking filtered by an unknown last name returns an empty list")
    public void listFilteredByUnknownNameIsEmpty() {
        Response response = bookings.list(Map.of("lastname", Bookings.uniqueLastName()));

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.jsonPath().getList("$").isEmpty(), "Expected no match: " + response.asString());
    }

    @Test(groups = "api", description = "GET /booking filtered by a check-in date before the booking's contains the created id")
    public void listFilteredByCheckinContainsCreatedBooking() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            // The checkin filter is exclusive (checkin=2027-03-01 does NOT match a booking checking in on
            // 2027-03-01), so filter by the day before. Narrow by name too: other bookings may share the date.
            Response response = bookings.list(Map.of(
                    "lastname", created.booking().lastname(),
                    "checkin", Bookings.DAY_BEFORE_CHECKIN));

            Assert.assertEquals(response.statusCode(), 200);
            response.then().body("bookingid", hasItem(created.id()));
        }
    }

    @Test(groups = "api", description = "GET /booking/{id} matches the booking JSON schema")
    public void bookingMatchesJsonSchema() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            bookings.get(created.id()).then()
                    .statusCode(200)
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/booking.json"));
        }
    }

    @Test(groups = "api", description = "GET /booking/{id} responds within the response time limit")
    public void getBookingIsFast() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.get(created.id());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertTrue(response.time() <= ApiLimits.MAX_RESPONSE_TIME.toMillis(),
                    "GET took " + response.time() + " ms, limit is " + ApiLimits.MAX_RESPONSE_TIME.toMillis() + " ms");
        }
    }

    @Test(groups = "api", description = "GET with an id that does not exist returns 404")
    public void unknownIdReturnsNotFound() {
        Assert.assertEquals(bookings.get(Bookings.MISSING_ID).statusCode(), 404);
    }

    @Test(groups = "api", description = "PUT with a body missing required fields is rejected with 400")
    public void updateWithIncompleteBodyIsBadRequest() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            Response response = bookings.update(created.id(),
                    new Booking("OnlyName", null, null, null, null, null), created.token());

            Assert.assertEquals(response.statusCode(), 400);
        }
    }

    @Test(groups = "api", description = "A malformed JSON body is rejected with 400 and creates nothing")
    public void malformedJsonIsBadRequest() {
        Response response = bookings.createRaw("{\"firstname\": ");

        Assert.assertEquals(response.statusCode(), 400);
    }

    @Test(groups = "api", description = "PATCH on an id that does not exist is rejected (405 by Restful-Booker)")
    public void patchUnknownIdIsRejected() {
        String token = new AuthClient().validToken();
        Response response = bookings.patch(Bookings.MISSING_ID, Map.of("firstname", "Ghost"), token);

        // Real behaviour: 405 Method Not Allowed. Never a 2xx.
        Assert.assertTrue(response.statusCode() >= 400 && response.statusCode() < 500,
                "Expected a 4xx, got " + response.statusCode());
    }
}
