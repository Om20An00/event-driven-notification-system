package com.om.notification.service.impl;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;
import com.om.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated push channel. Swap send() for an FCM/APNs call to go live.
 */
@Service
public class PushNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    @Override
    public NotificationType getType() {
        return NotificationType.PUSH;
    }

    @Override
    public void send(NotificationEvent event) {
        log.info("[PUSH] to={} title='{}' body='{}' eventId={}",
                event.getRecipient(), event.getSubject(), event.getMessage(), event.getEventId());
    }
}
