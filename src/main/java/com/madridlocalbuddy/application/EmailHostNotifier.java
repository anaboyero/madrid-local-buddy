package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.infrastructure.EmailSender;

public class EmailHostNotifier implements HostNotifier {

    private static final String HOST_EMAIL = "host@localhost";

    private final EmailSender emailSender;

    public EmailHostNotifier(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void notify(ExperienceRequest request) {
        String subject = "New experience request — " + request.experience().title();
        String nativeSpeakerLabel = request.visitor().nativeEnglishSpeaker() ? "yes" : "no";
        String body =
                """
                Experience: %s
                Visitor email: %s
                Native English speaker: %s
                Preferred date or time: %s
                """
                        .formatted(
                                request.experience().title(),
                                request.visitor().email(),
                                nativeSpeakerLabel,
                                request.comment())
                        .stripTrailing();

        try {
            emailSender.send(HOST_EMAIL, subject, body);
        } catch (RuntimeException ex) {
            throw new HostNotificationException("Unable to notify host");
        }
    }
}
