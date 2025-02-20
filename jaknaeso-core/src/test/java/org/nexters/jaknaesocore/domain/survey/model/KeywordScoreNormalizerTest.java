package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class KeywordScoreNormalizerTest {

  @Test
  void 특정_키워드의_점수를_정규화한다() {
    final Long surveyId = 1L;
    final List<KeywordScore> scores =
        List.of(
            KeywordScore.of(SUCCESS, BigDecimal.valueOf(2)),
            KeywordScore.of(SUCCESS, BigDecimal.valueOf(1)),
            KeywordScore.of(SUCCESS, BigDecimal.valueOf(0)),
            KeywordScore.of(SUCCESS, BigDecimal.valueOf(-1)),
            KeywordScore.of(SUCCESS, BigDecimal.valueOf(-2)));
    final OptionScoreMetrics metrics = OptionScoreMetrics.of(surveyId, scores);

    final BigDecimal actual1 = KeywordScoreNormalizer.normalize(BigDecimal.valueOf(-1), metrics);
    final BigDecimal actual2 = KeywordScoreNormalizer.normalize(BigDecimal.valueOf(1), metrics);

    assertAll(
        () -> then(actual1).isEqualTo(BigDecimal.valueOf(0.25)),
        () -> then(actual2).isEqualTo(BigDecimal.valueOf(0.75)));
  }
}
