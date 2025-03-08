package org.nexters.jaknaesoserver.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
public class ApiRequestResponseAop {
  private final ObjectMapper objectMapper;
  private static final ThreadLocal<Boolean> isFirstRequest = ThreadLocal.withInitial(() -> false);

  public ApiRequestResponseAop(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Pointcut("within(org.nexters.jaknaesoserver.domain..controller..*)")
  public void apiRestPointCut() {}

  @Around("apiRestPointCut()")
  public Object requestResponseLogging(ProceedingJoinPoint joinPoint) throws Throwable {
    boolean isFirst = !isFirstRequest.get();
    if (isFirst) {
      isFirstRequest.set(true);
    }
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
    String traceId = (String) request.getAttribute("traceId");
    Map<String, String> params = getParameter(request);
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    RequestResponseLogging reqResLogging =
        new RequestResponseLogging(
            traceId,
            className,
            methodName,
            request.getMethod(),
            request.getRequestURI(),
            params,
            objectMapper.readTree(request.getInputStream().readAllBytes()),
            null);
    try {
      Object result = joinPoint.proceed();
      if (isFirst) {
        if (result instanceof ResponseEntity) {
          reqResLogging.updateResponseBody(((ResponseEntity<?>) result).getBody());
        } else {
          reqResLogging.updateResponseBody("{}");
        }
        log.info(objectMapper.writeValueAsString(result));
      }
      return result;
    } catch (Throwable e) {
      if (isFirst) {
        log.info("{}", objectMapper.writeValueAsString(reqResLogging));
      }
      throw e;
    } finally {
      if (isFirst) {
        isFirstRequest.remove();
      }
    }
  }

  private Map<String, String> getParameter(HttpServletRequest request) {
    Map<String, String> jsonObject = new HashMap<>();
    Enumeration<String> paramNames = request.getParameterNames();

    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      String replaceParam = paramName.replaceAll("\\.", "-");
      jsonObject.put(replaceParam, request.getParameter(paramName));
    }
    return jsonObject;
  }
}
