package com.nevena.api.tests;

import java.util.Map;

import com.nevena.api.client.AuthClient;
import com.nevena.api.client.BookingClient;
import com.nevena.api.client.TestBooking;
import com.nevena.api.data.Bookings;
import com.nevena.api.model.Credentials;
import com.nevena.ecommerce.config.Config;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Restful-Booker behaviour that breaks REST conventions or its own docs. Each test asserts the CORRECT
 * behaviour, so it fails until the API is fixed. They are in "api-known-defect", which api.xml skips.
 * Run: mvn test -DsuiteXml=src/test/resources/api-known-defects.xml
 */
public class BookingApiDefectsTest {
    private final BookingClient bookings = new BookingClient();

    /** Removes a booking the API wrongly accepted, so negative tests don't leave data behind. */
    private static void cleanUp(Response response) {
        if (response.statusCode() == 200) {
            Integer id = response.path("bookingid");
            if (id != null) {
                TestBooking.deleteQuietly(id);
            }
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): DELETE /booking/{id} answers 201 Created. Nothing is created by a
     * delete; REST convention (and the API docs' own "success" semantics) call for 200 OK or 204 No Content.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: DELETE answers 200 or 204, not 201 Created")
    public void deleteAnswersWithNoContentOrOk() {
        try (TestBooking created = TestBooking.create(Bookings.valid())) {
            int status = bookings.delete(created.id(), created.token()).statusCode();

            Assert.assertTrue(status == 200 || status == 204, "DELETE returned " + status);
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): POST /booking with a body that is valid JSON but lacks required
     * fields (only firstname) crashes the server: 500 Internal Server Error with the text body
     * "Internal Server Error". Invalid client input should be a 400.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: creating a booking with missing fields answers 400, not 500")
    public void createWithMissingFieldsIsBadRequest() {
        Response response = bookings.create(Map.of("firstname", "OnlyName"));
        try {
            Assert.assertEquals(response.statusCode(), 400);
        } finally {
            cleanUp(response);
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): a booking whose checkout (2027-03-01) is BEFORE its check-in
     * (2027-03-05) is accepted and stored (200). It should be rejected with 400.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: a booking that checks out before it checks in is rejected with 400")
    public void createWithCheckoutBeforeCheckinIsBadRequest() {
        Response response = bookings.create(Bookings.withDates("2027-03-05", "2027-03-01"));
        try {
            Assert.assertEquals(response.statusCode(), 400);
        } finally {
            cleanUp(response);
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): a negative totalprice (-5) is accepted and stored (200).
     * It should be rejected with 400.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: a negative total price is rejected with 400")
    public void createWithNegativePriceIsBadRequest() {
        Response response = bookings.create(Bookings.withTotalPrice(-5));
        try {
            Assert.assertEquals(response.statusCode(), 400);
        } finally {
            cleanUp(response);
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): wrong data types are silently coerced instead of rejected. With
     * totalprice "abc", depositpaid "zz" and dates "nope", the API answers 200 and stores totalprice null,
     * depositpaid true and the dates "0NaN-aN-aN". It should answer 400.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: wrong field types are rejected with 400, not coerced")
    public void createWithWrongTypesIsBadRequest() {
        Map<String, Object> body = Map.of(
                "firstname", "Types", "lastname", Bookings.uniqueLastName(),
                "totalprice", "abc", "depositpaid", "zz",
                "bookingdates", Map.of("checkin", "nope", "checkout", "nope"));
        Response response = bookings.create(body);
        try {
            Assert.assertEquals(response.statusCode(), 400);
        } finally {
            cleanUp(response);
        }
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): DELETE on a booking that does not exist (here: one that was
     * already deleted) answers 405 Method Not Allowed. The method is allowed on the resource; the
     * resource is missing, so the answer should be 404 Not Found.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: deleting a booking that does not exist answers 404, not 405")
    public void deleteUnknownIdIsNotFound() {
        String token = new AuthClient().validToken();

        Assert.assertEquals(bookings.delete(Bookings.MISSING_ID, token).statusCode(), 404);
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): POST /auth with wrong credentials answers 200 OK with
     * {"reason":"Bad credentials"}. Failed authentication should be 401 Unauthorized.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: bad credentials answer 401, not 200")
    public void badCredentialsAnswerUnauthorized() {
        Response response = new AuthClient().login(new Credentials(Config.apiUsername(), "wrong-password"));

        Assert.assertEquals(response.statusCode(), 401);
    }

    /*
     * KNOWN DEFECT (found 2026-11-23): POST /booking with an Accept list that contains application/json
     * ("application/json, application/javascript, text/javascript, text/json", which is what REST Assured's
     * setAccept(ContentType.JSON) sends) answers 418 I'm a teapot. A list that includes a type the server
     * can produce must be served normally (200); a plain "application/json" works.
     */
    @Test(groups = "api-known-defect", description = "KNOWN DEFECT: an Accept list containing application/json is served, not answered with 418")
    public void acceptListWithJsonIsServed() {
        Response response = bookings.createAccepting(Bookings.valid(),
                "application/json, application/javascript, text/javascript, text/json");
        try {
            Assert.assertEquals(response.statusCode(), 200);
        } finally {
            cleanUp(response);
        }
    }
}
