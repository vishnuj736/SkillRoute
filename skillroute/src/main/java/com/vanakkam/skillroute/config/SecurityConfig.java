package com.vanakkam.skillroute.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF for stateless API testing
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Enable CORS so your React client won't block endpoints later
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Configure strict endpoint mapping rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all endpoints under /api/admin/ to bypass security filters during development
                        .requestMatchers("/api/admin/**").permitAll()

                        // CRITICAL FIX: Allow Spring Boot's internal error dispatching path to bypass auth.
                        // If your code throws an error internally, Spring redirects to /error.
                        // If /error is locked, it shows a 403 Forbidden instead of the actual error message!
                        .requestMatchers("/error").permitAll()

                        // Everything else stays locked down until we add JWT tokens
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Allows testing from any frontend origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}