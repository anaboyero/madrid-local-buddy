package com.madridlocalbuddy.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailSenderPropertiesTest {

    @Test
    void validateForStartup_logMode_succeeds() {
        EmailSenderProperties properties = new EmailSenderProperties();
        properties.setMode("log");

        assertDoesNotThrow(properties::validateForStartup);
    }

    @Test
    void validateForStartup_httpModeWithAllVars_succeeds() {
        EmailSenderProperties properties = completeHttpProperties();

        assertDoesNotThrow(properties::validateForStartup);
    }

    @Test
    void validateForStartup_httpModeMissingApiKey_throws() {
        EmailSenderProperties properties = completeHttpProperties();
        properties.setApiKey(null);

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, properties::validateForStartup);

        assertTrue(exception.getMessage().contains("EMAIL_API_KEY"));
    }

    @Test
    void validateForStartup_httpModeMissingHostEmail_throws() {
        EmailSenderProperties properties = completeHttpProperties();
        properties.setHostNotificationEmail("");

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, properties::validateForStartup);

        assertTrue(exception.getMessage().contains("HOST_NOTIFICATION_EMAIL"));
    }

    @Test
    void validateForStartup_httpModeMissingFrom_throws() {
        EmailSenderProperties properties = completeHttpProperties();
        properties.setFrom("  ");

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, properties::validateForStartup);

        assertTrue(exception.getMessage().contains("EMAIL_FROM"));
    }

    @Test
    void defaultMode_isLog() {
        EmailSenderProperties properties = new EmailSenderProperties();

        assertEquals("log", properties.getMode());
    }

    private static EmailSenderProperties completeHttpProperties() {
        EmailSenderProperties properties = new EmailSenderProperties();
        properties.setMode("http");
        properties.setHostNotificationEmail("host@example.com");
        properties.setFrom("onboarding@resend.dev");
        properties.setApiKey("re_test_key");
        return properties;
    }
}
