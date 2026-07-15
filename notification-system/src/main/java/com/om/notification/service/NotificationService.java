package com.om.notification.service;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;

/**
 * Strategy interface. Each channel (email/SMS/push) implements this;
 * the consumer picks the right implementation at runtime via getType().
 * Adding a new channel later (e.g. Slack, WhatsApp) means adding one
 * new implementation class -- nothing else in the pipeline changes.
 */
public interface NotificationService {

    NotificationType getType();

    void send(NotificationEvent event);
}
