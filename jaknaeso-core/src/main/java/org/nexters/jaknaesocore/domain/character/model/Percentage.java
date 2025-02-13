package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Percentage {

  private static final int SCALE = 2;
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private final BigDecimal value;

  private Percentage(final BigDecimal value) {
    this.value = value.setScale(SCALE, ROUNDING_MODE);
  }

  public static Percentage of(final BigDecimal value) {
    return new Percentage(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Percentage that = (Percentage) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
