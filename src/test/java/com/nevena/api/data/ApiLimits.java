package com.nevena.api.data;

import java.time.Duration;

/** Time limits API tests hold the service to. Generous on purpose: Restful-Booker is a free shared Heroku app. */
public final class ApiLimits {
    public static final Duration MAX_RESPONSE_TIME = Duration.ofSeconds(5);

    private ApiLimits() {
    }
}
