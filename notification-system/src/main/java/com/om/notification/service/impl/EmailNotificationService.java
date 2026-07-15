package com.om.notification.service.impl;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;
import com.om.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated email channel. Logs the send instead of calling a real provider
 * so the project runs end-to-end with zero external accounts/API keys.
 *
 * To wire up a real provider, replace the body of send() with a call to
 * an SES/SendGrid/SMTP client and keep the same interface contract.
 */
@Service
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationEvent event) {
        log.info("[EMAIL] to={} subject='{}' body='{}' eventId={}",
                event.getRecipient(), event.getSubject(), event.getMessage(), event.getEventId());
    }
}
