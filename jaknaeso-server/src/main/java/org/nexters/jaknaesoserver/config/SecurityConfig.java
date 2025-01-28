package org.nexters.jaknaesoserver.config;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesoserver.common.controller.PublicEndpoints;
import org.nexters.jaknaesoserver.domain.auth.filter.JwtAuthFilter;
import org.nexters.jaknaesoserver.domain.auth.handler.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private final SecurityExceptionHandler securityExceptionHandler;
  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(PublicEndpoints.getPatterns())
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(securityExceptionHandler)
                    .accessDeniedHandler(securityExceptionHandler))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
