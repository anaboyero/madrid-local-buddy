package com.madridlocalbuddy.config;

import com.madridlocalbuddy.application.EmailHostNotifier;
import com.madridlocalbuddy.application.ExperienceRequestMapper;
import com.madridlocalbuddy.application.HostNotifier;
import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequestValidator;
import com.madridlocalbuddy.infrastructure.EmailSender;
import com.madridlocalbuddy.infrastructure.LogEmailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

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
    EmailSender emailSender() {
        return new LogEmailSender();
    }

    @Bean
    HostNotifier hostNotifier(EmailSender emailSender) {
        return new EmailHostNotifier(emailSender);
    }
}
