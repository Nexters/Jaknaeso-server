package org.nexters.jaknaesocore.common.support.infrastructure;

import java.time.LocalDateTime;
import org.nexters.jaknaesocore.common.support.service.LocalDateTimeHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemLocalDateTimeHolder implements LocalDateTimeHolder {
  @Override
  public LocalDateTime now() {
    return LocalDateTime.now();
  }
}
