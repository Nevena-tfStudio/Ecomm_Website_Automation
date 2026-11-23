package com.nevena.api.tests;

import com.nevena.api.client.AuthClient;
import com.nevena.api.data.ApiLimits;
import com.nevena.api.model.Credentials;
import com.nevena.ecommerce.config.Config;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/** /ping and /auth. These tests don't extend BaseTest, so no browser starts. */
public class AuthApiTest {
    private final AuthClient auth = new AuthClient();

    @Test(groups = {"api", "smoke"}, description = "API health check: /ping answers 201 Created")
    public void pingAnswers() {
        Response response = auth.ping();

        // The API docs say /ping returns 201 Created
        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(response.asString(), "Created");
    }

    @Test(groups = "api", description = "/ping answers within the response time limit")
    public void pingIsFast() {
        Response response = auth.ping();

        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertTrue(response.time() <= ApiLimits.MAX_RESPONSE_TIME.toMillis(),
                "/ping took " + response.time() + " ms, limit is " + ApiLimits.MAX_RESPONSE_TIME.toMillis() + " ms");
    }

    @Test(groups = {"api", "smoke"}, description = "Valid credentials return a token")
    public void validCredentialsReturnToken() {
        Response response = auth.login(new Credentials(Config.apiUsername(), Config.apiPassword()));

        Assert.assertEquals(response.statusCode(), 200);
        String token = response.path("token");
        Assert.assertNotNull(token, "No token in " + response.asString());
        Assert.assertFalse(token.isBlank());
    }

    /*
     * Restful-Booker does not use 401 for bad credentials: it answers 200 with {"reason":"Bad credentials"}.
     * This test pins that real behaviour (see api-known-defects.xml for the REST-conventional expectation).
     */
    @Test(groups = "api", description = "Wrong credentials return 'Bad credentials' and no token")
    public void badCredentialsAreRejected() {
        Response response = auth.login(new Credentials(Config.apiUsername(), "wrong-password"));

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.path("reason"), "Bad credentials");
        Assert.assertNull(response.path("token"));
    }

    @Test(groups = "api", description = "Empty credentials return 'Bad credentials' and no token")
    public void emptyCredentialsAreRejected() {
        Response response = auth.login(new Credentials("", ""));

        Assert.assertEquals(response.path("reason"), "Bad credentials");
        Assert.assertNull(response.path("token"));
    }
}
