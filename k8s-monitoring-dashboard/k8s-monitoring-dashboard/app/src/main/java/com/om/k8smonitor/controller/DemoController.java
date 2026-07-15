package com.om.k8smonitor.controller;

import com.om.k8smonitor.metrics.CustomMetrics;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulated business endpoints. There's no real "order" persistence here —
 * the point of this service is to be something with realistic, varied
 * metrics (latency, error rate, throughput) for the dashboards and alerts
 * to have something to show. Swap this controller for real business logic
 * if you build on top of this.
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    private final CustomMetrics customMetrics;

    public DemoController(CustomMetrics customMetrics) {
        this.customMetrics = customMetrics;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "k8s-monitor-app");
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder() {
        return customMetrics.getOrderProcessingTimer().record(() -> {
            try {
                // Simulate variable processing time (20-200ms)
                Thread.sleep(ThreadLocalRandom.current().nextInt(20, 200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            customMetrics.recordOrderCreated();

            // Simulate an occasional failure (~10%) so error-rate panels/alerts have something to fire on
            boolean failed = ThreadLocalRandom.current().nextInt(100) < 10;
            customMetrics.decrementQueue();

            if (failed) {
                customMetrics.recordOrderFailed();
                return Map.of("status", "FAILED");
            }
            return Map.of("status", "CREATED");
        });
    }

    /**
     * Convenience endpoint to generate a burst of simulated traffic, e.g.
     *   curl -X POST http://localhost:8080/api/load/50
     * Useful for populating dashboards quickly during a demo/recording.
     */
    @PostMapping("/load/{requests}")
    public Map<String, String> generateLoad(@PathVariable int requests) {
        int capped = Math.min(requests, 500);
        for (int i = 0; i < capped; i++) {
            createOrder();
        }
        return Map.of("status", "generated " + capped + " simulated orders");
    }
}
