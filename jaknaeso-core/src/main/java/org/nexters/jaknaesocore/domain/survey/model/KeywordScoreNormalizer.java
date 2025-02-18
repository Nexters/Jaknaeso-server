package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class KeywordScoreNormalizer {

  public static BigDecimal normalize(final BigDecimal originalScore, final KeywordMetrics metrics) {
    var maxScore = metrics.getPositive();
    var minScore = metrics.getNegative();

    var numerator = originalScore.subtract(minScore);
    var denominator = maxScore.subtract(minScore);

    return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
  }
}
