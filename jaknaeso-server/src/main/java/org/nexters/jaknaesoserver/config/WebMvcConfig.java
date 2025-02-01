package org.nexters.jaknaesoserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private static final String LOCAL_URL = "http://localhost:3000";
  private static final String JAKNAESO_WEB_VERCEL_APP = "https://jaknaeso-web.vercel.app";

  @Value("${cors.origins.api-doc}")
  private String API_DOC_HOST;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(API_DOC_HOST, LOCAL_URL, JAKNAESO_WEB_VERCEL_APP)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
        .allowedHeaders("*");
  }
}
