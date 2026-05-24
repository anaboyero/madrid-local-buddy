package com.madridlocalbuddy.infrastructure;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class HttpEmailSender implements EmailSender {

    private static final String USER_AGENT = "madrid-local-buddy/0.0.1-SNAPSHOT";

    private final String apiUrl;
    private final String apiKey;
    private final String from;
    private final RestClient restClient;

    public HttpEmailSender(String apiUrl, String apiKey, String from, RestClient restClient) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.from = from;
        this.restClient = restClient;
    }

    @Override
    public void send(String to, String subject, String body) {
        ResendEmailRequest request = new ResendEmailRequest(from, List.of(to), subject, body);

        restClient
                .post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("User-Agent", USER_AGENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    throw new RuntimeException("Failed to send email: HTTP " + res.getStatusCode().value());
                })
                .toBodilessEntity();
    }
}
