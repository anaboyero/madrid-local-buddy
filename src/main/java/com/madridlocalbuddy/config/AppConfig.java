package com.madridlocalbuddy.config;

import com.madridlocalbuddy.domain.ExperienceCatalog;
import com.madridlocalbuddy.domain.ExperienceRequestValidator;
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
}
