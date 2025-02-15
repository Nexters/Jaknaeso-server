package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class KeywordMetricsTest {

  @Test
  void 키워드_개수_양의_점수의_합_음의_점수의_합을_계산한다() {
    final List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(-1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(-1)).build());

    final KeywordMetrics actual = KeywordMetrics.create(scores);

    then(actual)
        .extracting("cnt", "positive", "negative")
        .containsExactly(4, BigDecimal.valueOf(2), BigDecimal.valueOf(-2));
  }
}
