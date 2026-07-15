package com.om.k8smonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the sample workload that gets deployed to Kubernetes.
 *
 * This service represents "the thing being monitored" in the project:
 *  - Exposes standard JVM/process metrics via Spring Boot Actuator
 *  - Exposes custom business metrics (orders created/failed, processing latency)
 *  - Everything is scraped by Prometheus at /actuator/prometheus
 */
@SpringBootApplication
public class K8sMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sMonitorApplication.class, args);
    }
}
