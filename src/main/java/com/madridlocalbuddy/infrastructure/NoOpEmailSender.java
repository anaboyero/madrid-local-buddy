package com.madridlocalbuddy.infrastructure;

public class NoOpEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String body) {
        // Modo log: entrega sin envío real
    }
}
