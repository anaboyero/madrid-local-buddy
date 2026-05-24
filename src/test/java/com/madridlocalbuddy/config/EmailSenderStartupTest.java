package com.madridlocalbuddy.config;

import com.madridlocalbuddy.MadridLocalBuddyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class EmailSenderStartupTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(MadridLocalBuddyApplication.class);

    @Test
    void contextLoads_httpModeWithoutApiKey_fails() {
        contextRunner
                .withPropertyValues("EMAIL_SENDER_MODE=http")
                .run(context -> assertThat(context).hasFailed());
    }
}
