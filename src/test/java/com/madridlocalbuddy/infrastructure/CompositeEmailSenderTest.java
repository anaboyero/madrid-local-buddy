package com.madridlocalbuddy.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompositeEmailSenderTest {

    @Mock
    private EmailSender logSender;

    @Mock
    private EmailSender deliverySender;

    @Test
    void send_alwaysInvokesLogSender() {
        CompositeEmailSender sender = new CompositeEmailSender(logSender, new NoOpEmailSender());

        assertDoesNotThrow(() -> sender.send("host@localhost", "Subject", "Body"));

        verify(logSender).send("host@localhost", "Subject", "Body");
    }

    @Test
    void send_withHttpDelivery_invokesBothLogAndDelivery() {
        CompositeEmailSender sender = new CompositeEmailSender(logSender, deliverySender);

        sender.send("host@example.com", "Subject", "Body");

        verify(logSender).send("host@example.com", "Subject", "Body");
        verify(deliverySender).send("host@example.com", "Subject", "Body");
    }
}
