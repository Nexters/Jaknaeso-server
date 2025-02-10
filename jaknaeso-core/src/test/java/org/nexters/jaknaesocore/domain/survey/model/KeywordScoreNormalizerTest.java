package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.survey.model.KeywordStatistics.KeywordMetrics;

class KeywordScoreNormalizerTest {

  @Test
  void 키워드_점수를_정규화한다() {
    KeywordMetrics metrics = new KeywordMetrics();
    metrics.update(BigDecimal.valueOf(1));
    metrics.update(BigDecimal.valueOf(1));
    metrics.update(BigDecimal.valueOf(-1));
    metrics.update(BigDecimal.valueOf(-1));
    KeywordScoreNormalizer normalizer = new KeywordScoreNormalizer(BigDecimal.ONE, metrics);

    assertAll(
        () ->
            then(normalizer.normalize(BigDecimal.valueOf(-1))).isEqualTo(BigDecimal.valueOf(0.25)),
        () ->
            then(normalizer.normalize(BigDecimal.valueOf(1))).isEqualTo(BigDecimal.valueOf(0.75)));
  }
}
