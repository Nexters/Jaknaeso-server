package org.nexters.jaknaesocore.common.support.mock;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.service.LocalDateTimeHolder;

@RequiredArgsConstructor
public class TestLocalDateTimeHolder implements LocalDateTimeHolder {

  private final LocalDateTime localDateTime;

  public LocalDateTime now() {
    return localDateTime;
  }
}
