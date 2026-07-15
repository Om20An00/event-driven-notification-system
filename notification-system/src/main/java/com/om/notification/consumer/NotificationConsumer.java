package com.om.notification.consumer;

import com.om.notification.model.NotificationEvent;
import com.om.notification.service.NotificationServiceFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationServiceFactory serviceFactory;

    public NotificationConsumer(NotificationServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @KafkaListener(topics = "${notification.kafka.topic}", groupId = "${notification.kafka.consumer-group}")
    public void onMessage(ConsumerRecord<String, NotificationEvent> record, Acknowledgment ack) {
        NotificationEvent event = record.value();
        try {
            log.info("Received event {} (partition={}, offset={})", event.getEventId(),
                    record.partition(), record.offset());

            serviceFactory.resolve(event.getType()).send(event);

            // Only commit the offset once the notification has actually been
            // handed off successfully. If send() throws, the error handler
            // configured in KafkaConsumerConfig retries, then routes to the DLQ.
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process event {}: {}", event.getEventId(), ex.getMessage());
            throw ex;
        }
    }
}
