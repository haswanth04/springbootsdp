package com.klu.fsd.sdp.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.klu.fsd.sdp.model")
@EnableJpaRepositories(basePackages = "com.klu.fsd.sdp.repository")
public class JpaConfig {
    // This empty class ensures entity scanning works correctly
} 