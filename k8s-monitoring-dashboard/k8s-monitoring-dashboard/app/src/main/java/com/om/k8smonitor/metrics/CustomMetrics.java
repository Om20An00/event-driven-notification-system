package com.om.k8smonitor.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Registers application-level (business) metrics on top of Spring's
 * default JVM/HTTP metrics. These show up in /actuator/prometheus as:
 *
 *   orders_created_total
 *   orders_failed_total
 *   order_processing_duration_seconds{quantile="0.5|0.95|0.99"}
 *   order_processing_duration_seconds_count
 *   order_processing_duration_seconds_sum
 *   order_queue_size
 *
 * These are what the "Pod Monitoring" Grafana dashboard's application
 * panels are built from, alongside the infra-level container/pod metrics
 * that come from kube-state-metrics / cAdvisor.
 */
@Component
public class CustomMetrics {

    private final Counter ordersCreatedCounter;
    private final Counter ordersFailedCounter;
    private final Timer orderProcessingTimer;
    private final AtomicInteger queueSize = new AtomicInteger(0);

    public CustomMetrics(MeterRegistry registry) {
        this.ordersCreatedCounter = Counter.builder("orders_created_total")
                .description("Total number of orders successfully created")
                .register(registry);

        this.ordersFailedCounter = Counter.builder("orders_failed_total")
                .description("Total number of orders that failed processing")
                .register(registry);

        this.orderProcessingTimer = Timer.builder("order_processing_duration_seconds")
                .description("Time taken to process an order")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        registry.gauge("order_queue_size", queueSize);
    }

    public void recordOrderCreated() {
        ordersCreatedCounter.increment();
        queueSize.incrementAndGet();
    }

    public void recordOrderFailed() {
        ordersFailedCounter.increment();
    }

    public void decrementQueue() {
        queueSize.decrementAndGet();
    }

    public Timer getOrderProcessingTimer() {
        return orderProcessingTimer;
    }
}
