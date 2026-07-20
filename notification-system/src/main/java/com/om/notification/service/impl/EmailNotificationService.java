package com.om.notification.service.impl;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;
import com.om.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends real email via Gmail SMTP when notification.email.enabled=true.
 * Falls back to logging (no real send) when disabled or misconfigured,
 * so the project still runs with zero external accounts if you don't
 * want to wire up real credentials.
 */
@Service
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.email.from:}")
    private String fromAddress;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationEvent event) {
        if (!emailEnabled || fromAddress.isBlank()) {
            log.info("[EMAIL - simulated] to={} subject='{}' body='{}' eventId={}",
                    event.getRecipient(), event.getSubject(), event.getMessage(), event.getEventId());
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(event.getRecipient());
            mail.setSubject(event.getSubject() != null ? event.getSubject() : "Notification");
            mail.setText(event.getMessage());

            mailSender.send(mail);

            log.info("[EMAIL - sent] to={} subject='{}' eventId={}",
                    event.getRecipient(), event.getSubject(), event.getEventId());
        } catch (Exception ex) {
            // Re-throw so the retry/DLQ logic in KafkaConsumerConfig handles this
            // the same way any other channel failure is handled.
            log.error("Failed to send real email for eventId={}: {}", event.getEventId(), ex.getMessage());
            throw ex;
        }
    }
}

