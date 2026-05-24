package com.madridlocalbuddy.config;

import com.madridlocalbuddy.application.EmailHostNotifier;
import com.madridlocalbuddy.application.ExperienceRequestMapper;
import com.madridlocalbuddy.application.HostNotifier;
import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequestValidator;
import com.madridlocalbuddy.infrastructure.CompositeEmailSender;
import com.madridlocalbuddy.infrastructure.EmailSender;
import com.madridlocalbuddy.infrastructure.HttpEmailSender;
import com.madridlocalbuddy.infrastructure.LogEmailSender;
import com.madridlocalbuddy.infrastructure.NoOpEmailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    private static final String LOG_MODE_RECIPIENT = "host@localhost";

    @Bean
    ExperienceCatalog experienceCatalog() {
        return new ExperienceCatalog();
    }

    @Bean
    ExperienceRequestValidator experienceRequestValidator() {
        return new ExperienceRequestValidator();
    }

    @Bean
    ExperienceRequestMapper experienceRequestMapper() {
        return new ExperienceRequestMapper();
    }

    @Bean
    EmailSenderProperties emailSenderProperties(Environment environment) {
        EmailSenderProperties properties = new EmailSenderProperties();
        properties.setMode(environment.getProperty("EMAIL_SENDER_MODE", "log"));
        properties.setHostNotificationEmail(environment.getProperty("HOST_NOTIFICATION_EMAIL"));
        properties.setFrom(environment.getProperty("EMAIL_FROM"));
        properties.setApiKey(environment.getProperty("EMAIL_API_KEY"));
        String apiUrl = environment.getProperty("EMAIL_API_URL");
        if (apiUrl != null && !apiUrl.isBlank()) {
            properties.setApiUrl(apiUrl);
        }
        properties.validateForStartup();
        return properties;
    }

    @Bean
    EmailSender emailSender(EmailSenderProperties properties) {
        LogEmailSender logSender = new LogEmailSender();
        EmailSender deliverySender = properties.isHttpMode()
                ? new HttpEmailSender(
                        properties.getApiUrl(),
                        properties.getApiKey(),
                        properties.getFrom(),
                        RestClient.builder().build())
                : new NoOpEmailSender();
        return new CompositeEmailSender(logSender, deliverySender);
    }

    @Bean
    HostNotifier hostNotifier(EmailSender emailSender, EmailSenderProperties properties) {
        String recipient = properties.isHttpMode() ? properties.getHostNotificationEmail() : LOG_MODE_RECIPIENT;
        return new EmailHostNotifier(emailSender, recipient);
    }
}
