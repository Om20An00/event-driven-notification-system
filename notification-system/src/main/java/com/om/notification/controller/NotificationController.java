package com.om.notification.controller;

import com.om.notification.dto.NotificationRequest;
import com.om.notification.model.NotificationEvent;
import com.om.notification.service.NotificationProducerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationProducerService producerService;

    public NotificationController(NotificationProducerService producerService) {
        this.producerService = producerService;
    }

    /**
     * Accepts a notification request, wraps it as an event, and hands it off to
     * Kafka. Returns 202 Accepted immediately -- delivery happens asynchronously
     * on the consumer side, decoupling the caller from downstream latency/failures.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> send(@Valid @RequestBody NotificationRequest request) {
        NotificationEvent event = new NotificationEvent(
                request.getType(),
                request.getRecipient(),
                request.getSubject(),
                request.getMessage(),
                request.getMetadata()
        );

        producerService.publish(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "eventId", event.getEventId(),
                        "status", "QUEUED"
                ));
    }
}
