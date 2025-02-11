package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.model.KeywordStatistics.KeywordMetrics;

@RequiredArgsConstructor
public class KeywordScoreNormalizer {

  private final BigDecimal weight;
  private final KeywordMetrics metrics;

  public BigDecimal normalize(final BigDecimal originalScore) {
    var maxScore = metrics.getPositive().multiply(weight);
    var minScore = metrics.getNegative().multiply(weight);

    var numerator = originalScore.subtract(minScore);
    var denominator = maxScore.subtract(minScore);
    return numerator.divide(denominator, 2, RoundingMode.HALF_UP).multiply(weight);
  }
}
