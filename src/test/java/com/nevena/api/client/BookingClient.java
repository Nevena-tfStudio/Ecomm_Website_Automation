package com.nevena.api.client;

import java.util.Map;

import com.nevena.api.model.Booking;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/** Wraps the /booking endpoints. Methods return the raw Response so tests decide what to assert. */
public class BookingClient {
    private static RequestSpecification request() {
        return RestAssured.given().spec(ApiSpec.create());
    }

    private static RequestSpecification authorized(String token) {
        return request().cookie("token", token);
    }

    @Step("POST /booking")
    public Response create(Object body) {
        return request().body(body).post("/booking");
    }

    @Step("GET /booking/{id}")
    public Response get(int id) {
        return request().get("/booking/{id}", id);
    }

    @Step("GET /booking with filters {filters}")
    public Response list(Map<String, String> filters) {
        return request().queryParams(filters).get("/booking");
    }

    @Step("PUT /booking/{id}")
    public Response update(int id, Booking booking, String token) {
        return authorized(token).body(booking).put("/booking/{id}", id);
    }

    @Step("PUT /booking/{id} without a token")
    public Response updateWithoutToken(int id, Booking booking) {
        return request().body(booking).put("/booking/{id}", id);
    }

    @Step("PATCH /booking/{id}")
    public Response patch(int id, Map<String, Object> changes, String token) {
        return authorized(token).body(changes).patch("/booking/{id}", id);
    }

    @Step("PATCH /booking/{id} without a token")
    public Response patchWithoutToken(int id, Map<String, Object> changes) {
        return request().body(changes).patch("/booking/{id}", id);
    }

    @Step("DELETE /booking/{id}")
    public Response delete(int id, String token) {
        return authorized(token).delete("/booking/{id}", id);
    }

    @Step("DELETE /booking/{id} without a token")
    public Response deleteWithoutToken(int id) {
        return request().delete("/booking/{id}", id);
    }

    /** Sends the text as the body unchanged, to test malformed JSON. */
    @Step("POST /booking with a raw body")
    public Response createRaw(String rawBody) {
        return request().body(rawBody).post("/booking");
    }

    /** Sends the given Accept header value instead of the default, to test content negotiation. */
    @Step("POST /booking with Accept: {accept}")
    public Response createAccepting(Object body, String accept) {
        return request().header("Accept", accept).body(body).post("/booking");
    }
}
