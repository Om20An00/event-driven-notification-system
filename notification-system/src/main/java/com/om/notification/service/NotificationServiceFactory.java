package com.om.notification.service;

import com.om.notification.model.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationServiceFactory {

    private final Map<NotificationType, NotificationService> services;

    public NotificationServiceFactory(List<NotificationService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(NotificationService::getType, Function.identity()));
    }

    public NotificationService resolve(NotificationType type) {
        NotificationService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No NotificationService registered for type: " + type);
        }
        return service;
    }
}
