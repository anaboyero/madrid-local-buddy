package com.madridlocalbuddy.config;

public class EmailSenderProperties {

    public static final String DEFAULT_API_URL = "https://api.resend.com/emails";

    private String mode = "log";
    private String hostNotificationEmail;
    private String from;
    private String apiKey;
    private String apiUrl = DEFAULT_API_URL;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getHostNotificationEmail() {
        return hostNotificationEmail;
    }

    public void setHostNotificationEmail(String hostNotificationEmail) {
        this.hostNotificationEmail = hostNotificationEmail;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public boolean isHttpMode() {
        return "http".equalsIgnoreCase(mode);
    }

    public void validateForStartup() {
        if (!isHttpMode()) {
            return;
        }
        requireNonBlank(hostNotificationEmail, "HOST_NOTIFICATION_EMAIL");
        requireNonBlank(from, "EMAIL_FROM");
        requireNonBlank(apiKey, "EMAIL_API_KEY");
    }

    private static void requireNonBlank(String value, String variableName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + variableName);
        }
    }
}
