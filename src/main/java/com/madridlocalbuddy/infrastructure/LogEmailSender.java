package com.madridlocalbuddy.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("Email notification — to: {}, subject: {}, body: {}", to, subject, body);
    }
}
