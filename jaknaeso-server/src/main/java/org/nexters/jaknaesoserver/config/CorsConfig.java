package org.nexters.jaknaesoserver.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  private static final String LOCAL_URL = "http://localhost:3000";
  private static final String JAKNAESO_WEB_VERCEL_APP = "https://jaknaeso-web.vercel.app";

  @Value("${cors.origins.api-doc}")
  private String API_DOC_HOST;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    final CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(API_DOC_HOST, LOCAL_URL, JAKNAESO_WEB_VERCEL_APP));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
    corsConfiguration.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
