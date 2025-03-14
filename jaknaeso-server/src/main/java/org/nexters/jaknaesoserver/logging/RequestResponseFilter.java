package org.nexters.jaknaesoserver.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestResponseFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  public RequestResponseFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = UUID.randomUUID().toString();
    try {
      CachedBodyHttpServletRequest requestWrapper = new CachedBodyHttpServletRequest(request);
      requestWrapper.setAttribute("traceId", traceId);
      filterChain.doFilter(requestWrapper, response);
    } catch (Exception e) {
      log.error(e.getMessage());
      CustomException customException = new CustomException(ErrorType.DEFAULT_ERROR);
      try {
        PrintWriter printWriter = response.getWriter();
        printWriter.print(objectMapper.writeValueAsString(customException));
        printWriter.flush();
      } catch (IOException ex) {
        log.warn("IOException Occur");
        throw new RuntimeException();
      }
    }
  }
}
