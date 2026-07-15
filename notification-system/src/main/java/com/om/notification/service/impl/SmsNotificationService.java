package com.om.notification.service.impl;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;
import com.om.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated SMS channel. Swap send() for a Twilio/SNS call to go live.
 */
@Service
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(NotificationEvent event) {
        log.info("[SMS] to={} body='{}' eventId={}",
                event.getRecipient(), event.getMessage(), event.getEventId());
    }
}
