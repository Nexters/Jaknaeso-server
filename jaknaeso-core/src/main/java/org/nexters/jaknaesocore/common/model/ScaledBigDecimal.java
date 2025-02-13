package org.nexters.jaknaesocore.common.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;

@Getter
public class ScaledBigDecimal {

  private static final int SCALE = 2;
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private final BigDecimal value;

  private ScaledBigDecimal(final BigDecimal value) {
    this.value = value.setScale(SCALE, ROUNDING_MODE);
  }

  public static ScaledBigDecimal of(final BigDecimal value) {
    return new ScaledBigDecimal(value);
  }

  public ScaledBigDecimal add(final BigDecimal operand) {
    return new ScaledBigDecimal(value.add(operand));
  }

  public ScaledBigDecimal multiply(final BigDecimal operand) {
    return new ScaledBigDecimal(value.multiply(operand));
  }

  public ScaledBigDecimal subtract(final BigDecimal operand) {
    return new ScaledBigDecimal(value.subtract(operand));
  }

  public ScaledBigDecimal divide(final BigDecimal operand) {
    return new ScaledBigDecimal(value.divide(operand, SCALE, ROUNDING_MODE));
  }
}
