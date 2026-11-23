package com.nevena.api.client;

import com.nevena.api.model.Booking;
import com.nevena.api.model.CreatedBooking;

/**
 * A booking created for one test and deleted when the try block ends:
 * <pre>try (TestBooking created = TestBooking.create(Bookings.valid())) { ... }</pre>
 * It lives in a local variable (never a field), so parallel tests don't share it.
 */
public final class TestBooking implements AutoCloseable {
    private final BookingClient bookings = new BookingClient();
    private final String token;
    private final int id;
    private final Booking sent;

    private TestBooking(String token, int id, Booking sent) {
        this.token = token;
        this.id = id;
        this.sent = sent;
    }

    public static TestBooking create(Booking booking) {
        String token = new AuthClient().validToken();
        CreatedBooking created = new BookingClient().create(booking)
                .then().statusCode(200)
                .extract().as(CreatedBooking.class);
        return new TestBooking(token, created.bookingid(), booking);
    }

    public int id() {
        return id;
    }

    public Booking booking() {
        return sent;
    }

    public String token() {
        return token;
    }

    /** Deletes the booking. A booking the test already deleted (404/405) is fine. */
    @Override
    public void close() {
        bookings.delete(id, token);
    }

    /** Deletes a booking the API accepted by mistake in a negative test (the id comes from its response). */
    public static void deleteQuietly(int id) {
        new BookingClient().delete(id, new AuthClient().validToken());
    }
}
