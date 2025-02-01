package org.nexters.jaknaesoserver.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesoserver.common.controller.PublicEndpoints;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.nexters.jaknaesoserver.domain.auth.service.JwtParser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtParser jwtParser;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    return PublicEndpoints.getAllPaths().stream()
        .anyMatch(p -> new AntPathMatcher().match(p, path));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = extractToken(request);
      Long userId = jwtParser.extractIdFromToken(token);
      UsernamePasswordAuthenticationToken authentication = createAuthentication(userId);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (CustomException e) {
      if (!e.equals(CustomException.INCORRECT_TOKEN_FORMAT)) {
        SecurityContextHolder.clearContext();
      }
      request.setAttribute("exception", e);
    }
    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      throw CustomException.INCORRECT_TOKEN_FORMAT;
    }
    return header.substring(BEARER_PREFIX.length());
  }

  private UsernamePasswordAuthenticationToken createAuthentication(Long userId) {
    CustomUserDetails userDetails = new CustomUserDetails(userId);
    Object credentials = null;
    List<GrantedAuthority> authorities = Collections.emptyList();
    return new UsernamePasswordAuthenticationToken(userDetails, credentials, authorities);
  }
}
