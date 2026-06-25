package com.vanakkam.skillroute.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimitConfig {

    // Tracks request counts per IP address
    @Bean
    public Map<String, AtomicInteger> requestCountMap() {
        return new ConcurrentHashMap<>();
    }
}