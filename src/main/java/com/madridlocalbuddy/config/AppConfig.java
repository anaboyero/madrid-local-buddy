package com.madridlocalbuddy.config;

import com.madridlocalbuddy.domain.ExperienceCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    ExperienceCatalog experienceCatalog() {
        return new ExperienceCatalog();
    }
}
