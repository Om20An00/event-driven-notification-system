package com.om.notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable event that flows through Kafka from producer -> broker -> consumer.
 *
 * eventId doubles as an idempotency key. It has no effect today, but it is the
 * hook a future Redis-backed deduplication filter (SETNX eventId) would use to
 * make consumers safe against Kafka's at-least-once redelivery.
 */
public class NotificationEvent implements Serializable {

    private String eventId;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String message;
    private Map<String, String> metadata;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    public NotificationEvent() {
    }

    public NotificationEvent(NotificationType type, String recipient, String subject,
                              String message, Map<String, String> metadata) {
        this.eventId = UUID.randomUUID().toString();
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.metadata = metadata;
        this.createdAt = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "eventId='" + eventId + '\'' +
                ", type=" + type +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
