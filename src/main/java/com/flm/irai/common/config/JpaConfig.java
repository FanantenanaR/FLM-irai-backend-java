package com.flm.irai.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Configuration JPA pour activer l'audit automatique (@CreatedDate, @LastModifiedDate)
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
