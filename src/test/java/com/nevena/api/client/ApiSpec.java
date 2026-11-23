package com.nevena.api.client;

import com.nevena.ecommerce.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/** The request settings every API call shares. A new spec is built per client, so threads never share state. */
final class ApiSpec {
    private ApiSpec() {
    }

    static RequestSpecification create() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.apiBaseUrl())
                .setContentType(ContentType.JSON)
                // A plain header: setAccept(ContentType.JSON) would send a list of JSON types, which
                // Restful-Booker answers with 418 on POST /booking (see BookingApiDefectsTest)
                .addHeader("Accept", "application/json")
                // Attaches every request and response to the Allure report
                .addFilter(new AllureRestAssured())
                .build();
    }
}
