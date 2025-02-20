package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class KeywordScoreNormalizer {

  public static BigDecimal normalize(
      final BigDecimal originalScore, final OptionScoreMetrics metrics) {
    var maxScore = metrics.maxScore();
    var minScore = metrics.minScore();
    return normalize(originalScore, maxScore, minScore);
  }

  public static BigDecimal normalize(
      final BigDecimal originalScore, final BigDecimal maxScore, final BigDecimal minScore) {
    var numerator = originalScore.subtract(minScore);
    var denominator = maxScore.subtract(minScore);

    return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
  }
}
