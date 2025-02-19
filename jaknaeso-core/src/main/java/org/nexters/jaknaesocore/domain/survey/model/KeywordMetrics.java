package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class KeywordMetrics {

  private BigDecimal positive = BigDecimal.ZERO;
  private BigDecimal negative = BigDecimal.ZERO;

  public static KeywordMetrics create(final List<KeywordScore> scores) {
    final KeywordMetrics metrics = new KeywordMetrics();
    scores.forEach(score -> metrics.update(score.getScore()));
    return metrics;
  }

  private void update(final BigDecimal score) {
    if (score.compareTo(BigDecimal.ZERO) > 0) {
      positive = positive.add(score);
    } else if (score.compareTo(BigDecimal.ZERO) < 0) {
      negative = negative.add(score);
    }
  }
}
