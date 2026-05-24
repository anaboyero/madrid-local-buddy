package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.infrastructure.EmailSender;

public class EmailHostNotifier implements HostNotifier {

    private final EmailSender emailSender;

    public EmailHostNotifier(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void notify(ExperienceRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
