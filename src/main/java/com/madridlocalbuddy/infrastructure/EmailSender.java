package com.madridlocalbuddy.infrastructure;

public interface EmailSender {

    void send(String to, String subject, String body);
}
