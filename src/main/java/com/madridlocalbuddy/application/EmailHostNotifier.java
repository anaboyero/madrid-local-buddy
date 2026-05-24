package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.infrastructure.EmailSender;

public class EmailHostNotifier implements HostNotifier {

    private static final String DEFAULT_HOST_EMAIL = "host@localhost";

    private final EmailSender emailSender;
    private final String hostNotificationEmail;

    public EmailHostNotifier(EmailSender emailSender) {
        this(emailSender, DEFAULT_HOST_EMAIL);
    }

    public EmailHostNotifier(EmailSender emailSender, String hostNotificationEmail) {
        this.emailSender = emailSender;
        this.hostNotificationEmail = hostNotificationEmail;
    }

    @Override
    public void notify(ExperienceRequest request) {
        String subject = "You got a new experience request: " + request.experience().title();
        String nativeSpeakerLabel = request.visitor().nativeEnglishSpeaker() ? "yes" : "no";
        String body =
                """
                Experience: %s
                Description: %s
                Visitor email: %s
                Native English speaker: %s
                Preferred date or time: %s
                """
                        .formatted(
                                request.experience().title(),
                                request.experience().description(),
                                request.visitor().email(),
                                nativeSpeakerLabel,
                                request.comment())
                        .stripTrailing();

        try {
            emailSender.send(hostNotificationEmail, subject, body);
        } catch (RuntimeException ex) {
            throw new HostNotificationException("Unable to notify host");
        }
    }
}
