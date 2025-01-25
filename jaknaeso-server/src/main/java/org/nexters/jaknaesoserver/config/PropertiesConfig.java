package org.nexters.jaknaesoserver.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(
    basePackages = "org.nexters.jaknaesoserver"
)
public class PropertiesConfig {
}