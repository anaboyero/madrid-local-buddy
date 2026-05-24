package com.madridlocalbuddy.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpStatus;

class HttpEmailSenderTest {

    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final String CUSTOM_URL = "https://custom.example.com/send";
    private static final String API_KEY = "re_test_key";
    private static final String FROM = "onboarding@resend.dev";

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    @AfterEach
    void verifyServer() {
        mockServer.verify();
    }

    @Test
    void send_whenResendReturns200_completesWithoutException() {
        mockServer
                .expect(requestTo(RESEND_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("User-Agent", "madrid-local-buddy/0.0.1-SNAPSHOT"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        """
                        {
                          "from": "onboarding@resend.dev",
                          "to": ["host@example.com"],
                          "subject": "Test subject",
                          "text": "Test body"
                        }
                        """))
                .andRespond(withSuccess("{\"id\":\"email-id\"}", MediaType.APPLICATION_JSON));

        HttpEmailSender sender =
                new HttpEmailSender(RESEND_URL, API_KEY, FROM, restClientBuilder.build());

        assertDoesNotThrow(() -> sender.send("host@example.com", "Test subject", "Test body"));
    }

    @Test
    void send_whenResendReturns401_throws() {
        mockServer
                .expect(requestTo(RESEND_URL))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        HttpEmailSender sender =
                new HttpEmailSender(RESEND_URL, API_KEY, FROM, restClientBuilder.build());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> sender.send("host@example.com", "Subject", "Body"));
        assertFalse(exception instanceof UnsupportedOperationException);
    }

    @Test
    void send_whenResendReturns500_throws() {
        mockServer
                .expect(requestTo(RESEND_URL))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        HttpEmailSender sender =
                new HttpEmailSender(RESEND_URL, API_KEY, FROM, restClientBuilder.build());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> sender.send("host@example.com", "Subject", "Body"));
        assertFalse(exception instanceof UnsupportedOperationException);
    }

    @Test
    void send_usesConfiguredApiUrl() {
        mockServer
                .expect(requestTo(CUSTOM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":\"email-id\"}", MediaType.APPLICATION_JSON));

        HttpEmailSender sender =
                new HttpEmailSender(CUSTOM_URL, API_KEY, FROM, restClientBuilder.build());

        assertDoesNotThrow(() -> sender.send("host@example.com", "Subject", "Body"));
    }
}
