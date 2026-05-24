package com.madridlocalbuddy.application;

import com.madridlocalbuddy.domain.ExperienceRequest;
import com.madridlocalbuddy.domain.Visitor;
import com.madridlocalbuddy.infrastructure.EmailSender;
import com.madridlocalbuddy.support.ContractCatalog;
import com.madridlocalbuddy.support.ContractRequests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailHostNotifierTest {

    private static final String HOST_RECIPIENT = "host@example.com";

    private static final String EXPECTED_SUBJECT = "You got a new experience request: Cinema";

    private static final String EXPECTED_BODY =
            """
            Experience: Cinema
            Description: An evening at a local cinema — film and conversation in English.
            Visitor email: visitor@example.com
            Native English speaker: yes
            Preferred date or time: Saturday afternoon would work best for me""";

    @Mock
    private EmailSender emailSender;

    @Test
    void notify_sendsFullSubjectAndBodyToConfiguredRecipient() {
        EmailHostNotifier notifier = new EmailHostNotifier(emailSender, HOST_RECIPIENT);

        notifier.notify(ContractRequests.VALID_DOMAIN);

        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(toCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        assertEquals(HOST_RECIPIENT, toCaptor.getValue());
        assertEquals(EXPECTED_SUBJECT, subjectCaptor.getValue());
        assertEquals(EXPECTED_BODY, bodyCaptor.getValue());
    }

    @Test
    void notify_whenVisitorIsNotNativeEnglishSpeaker_bodyContainsNo() {
        EmailHostNotifier notifier = new EmailHostNotifier(emailSender, HOST_RECIPIENT);
        ExperienceRequest request = new ExperienceRequest(
                ContractCatalog.CINEMA,
                new Visitor("visitor@example.com", false),
                "Saturday afternoon would work best for me");

        notifier.notify(request);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(anyString(), anyString(), bodyCaptor.capture());

        String body = bodyCaptor.getValue();
        assertEquals(
                """
                Experience: Cinema
                Description: An evening at a local cinema — film and conversation in English.
                Visitor email: visitor@example.com
                Native English speaker: no
                Preferred date or time: Saturday afternoon would work best for me""",
                body);
    }

    @Test
    void notify_whenEmailSenderFails_throwsHostNotificationException() {
        EmailHostNotifier notifier = new EmailHostNotifier(emailSender, HOST_RECIPIENT);
        doThrow(new RuntimeException("send failed"))
                .when(emailSender)
                .send(anyString(), anyString(), anyString());

        HostNotificationException exception =
                assertThrows(HostNotificationException.class, () -> notifier.notify(ContractRequests.VALID_DOMAIN));

        assertEquals("Unable to notify host", exception.getMessage());
    }
}
