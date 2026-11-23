package com.nevena.ecommerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Reads test settings from src/test/resources/config.properties.
 * A system property with the same key (-Dkey=value) wins over the file.
 */
public final class Config {
    private static final Properties FILE_VALUES = load();

    private Config() {
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String username() {
        return get("username");
    }

    public static String password() {
        return get("password");
    }

    /** Base URL of the API under test (API tests only). */
    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    public static String apiUsername() {
        return get("api.username");
    }

    public static String apiPassword() {
        return get("api.password");
    }

    /** "chrome" or "firefox". */
    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds")));
    }

    public static String get(String key) {
        String value = System.getProperty(key, FILE_VALUES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing config value '" + key + "' in config.properties or -D" + key);
        }
        return value.trim();
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the test classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read config.properties", e);
        }
        return properties;
    }
}
