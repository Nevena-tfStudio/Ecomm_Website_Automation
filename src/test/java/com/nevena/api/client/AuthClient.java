package com.nevena.api.client;

import com.nevena.api.model.Credentials;
import com.nevena.ecommerce.config.Config;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/** Wraps /auth and /ping. */
public class AuthClient {
    @Step("GET /ping")
    public Response ping() {
        return RestAssured.given().spec(ApiSpec.create()).get("/ping");
    }

    @Step("POST /auth")
    public Response login(Credentials credentials) {
        return RestAssured.given().spec(ApiSpec.create()).body(credentials).post("/auth");
    }

    /** A valid admin token, read from the config. */
    public String validToken() {
        return login(new Credentials(Config.apiUsername(), Config.apiPassword()))
                .then().statusCode(200)
                .extract().path("token");
    }
}
