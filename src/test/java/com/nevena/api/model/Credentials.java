package com.nevena.api.model;

/** Body of POST /auth. */
public record Credentials(String username, String password) {
}
