package com.madridlocalbuddy.application;

import com.madridlocalbuddy.infrastructure.EmailSender;
import com.madridlocalbuddy.support.ContractRequests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailHostNotifierTest {

    @Mock
    private EmailSender emailSender;

    @Test
    void notify_sendsEmailWithExperienceAndVisitorDetails() {
        EmailHostNotifier notifier = new EmailHostNotifier(emailSender);

        notifier.notify(ContractRequests.VALID_DOMAIN);

        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(org.mockito.ArgumentMatchers.anyString(), subjectCaptor.capture(), bodyCaptor.capture());

        String subject = subjectCaptor.getValue();
        String body = bodyCaptor.getValue();

        assertTrue(subject.contains("Cinema"));
        assertTrue(body.contains("visitor@example.com"));
        assertTrue(body.contains("Saturday afternoon would work best for me"));
        assertTrue(body.contains("native English speaker") || body.contains("Native English speaker"));
    }
}
