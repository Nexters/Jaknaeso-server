package org.nexters.jaknaesoserver.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtParser jwtParser;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = extractToken(request);

    try {
      Long userId = jwtParser.extractIdFromToken(token);
      UsernamePasswordAuthenticationToken authentication = createAuthentication(userId);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (CustomException e) {
      SecurityContextHolder.clearContext();
      request.setAttribute("exception", e);
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    try {
      String header = request.getHeader(AUTHORIZATION_HEADER);
      if (header == null || !header.startsWith(BEARER_PREFIX)) {
        throw CustomException.INCORRECT_TOKEN_FORMAT;
      }
      return header.substring(BEARER_PREFIX.length());
    } catch (Exception e) {
      throw CustomException.INTERNAL_SERVER_ERROR;
    }
  }

  private UsernamePasswordAuthenticationToken createAuthentication(Long userId) {
    Long principal = userId;
    Object credentials = null;
    List<GrantedAuthority> authorities = Collections.emptyList();
    return new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
  }
}
