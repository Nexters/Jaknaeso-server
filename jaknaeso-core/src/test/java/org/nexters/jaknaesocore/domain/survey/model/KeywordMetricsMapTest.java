package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.ADVENTURE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SECURITY;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KeywordMetricsMapTest {

  @Test
  void 키워드_점수_리스트를_바탕으로_키워드_지표_맵을_생성한다() {
    final List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(-1)).build(),
            KeywordScore.builder().keyword(SECURITY).score(BigDecimal.valueOf(1)).build());

    final Map<Keyword, KeywordMetrics> actual = KeywordMetricsMap.generate(scores);

    assertAll(
        () ->
            then(actual.get(ADVENTURE))
                .extracting("cnt", "positive", "negative")
                .containsExactly(2, BigDecimal.valueOf(1), BigDecimal.valueOf(-1)),
        () ->
            then(actual.get(SECURITY))
                .extracting("cnt", "positive", "negative")
                .containsExactly(1, BigDecimal.valueOf(1), BigDecimal.valueOf(0)));
  }
}
