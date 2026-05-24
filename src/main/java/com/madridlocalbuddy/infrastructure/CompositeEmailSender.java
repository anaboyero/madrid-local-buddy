package com.madridlocalbuddy.infrastructure;

public class CompositeEmailSender implements EmailSender {

    private final EmailSender logSender;
    private final EmailSender deliverySender;

    public CompositeEmailSender(EmailSender logSender, EmailSender deliverySender) {
        this.logSender = logSender;
        this.deliverySender = deliverySender;
    }

    @Override
    public void send(String to, String subject, String body) {
        logSender.send(to, subject, body);
        deliverySender.send(to, subject, body);
    }
}
