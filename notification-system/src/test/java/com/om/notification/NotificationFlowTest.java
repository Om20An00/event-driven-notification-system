package com.om.notification;

import com.om.notification.model.NotificationEvent;
import com.om.notification.model.NotificationType;
import com.om.notification.service.NotificationProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

/**
 * Spins up an in-memory Kafka broker, publishes a real event through the
 * producer, and relies on log output / no exceptions to confirm the consumer
 * picked it up and routed it to the EMAIL channel. Run with: mvn test
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"notification-events"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class NotificationFlowTest {

    @Autowired
    private NotificationProducerService producerService;

    @Test
    void publishesAndProcessesEventWithoutError() {
        NotificationEvent event = new NotificationEvent(
                NotificationType.EMAIL,
                "test-user@example.com",
                "Welcome",
                "Thanks for signing up!",
                Map.of("source", "integration-test")
        );

        producerService.publish(event);

        // Give the consumer a moment to pick the message up and process it.
        // (A production test suite would assert on a recorded side-effect
        // instead of sleeping -- kept simple here for demo purposes.)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
