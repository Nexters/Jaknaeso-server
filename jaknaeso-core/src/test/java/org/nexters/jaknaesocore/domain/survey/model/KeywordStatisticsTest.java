package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.ADVENTURE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SECURITY;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.UNIVERSALISM;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.survey.model.KeywordStatistics.KeywordMetrics;

class KeywordStatisticsTest {

  @Test
  void 키워드_지표를_계산한다() {
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(-1)).build(),
            KeywordScore.builder().keyword(SECURITY).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(UNIVERSALISM).score(BigDecimal.valueOf(-1)).build());
    KeywordStatistics statistics = new KeywordStatistics(scores);

    Map<Keyword, KeywordMetrics> actual = statistics.getMetrics();

    assertAll(
        () -> then(actual).hasSize(3),
        () ->
            then(actual.get(ADVENTURE))
                .extracting("cnt", "positive", "negative")
                .containsExactly(2, BigDecimal.valueOf(1), BigDecimal.valueOf(-1)),
        () ->
            then(actual.get(SECURITY))
                .extracting("cnt", "positive", "negative")
                .containsExactly(1, BigDecimal.valueOf(1), BigDecimal.valueOf(0)),
        () ->
            then(actual.get(UNIVERSALISM))
                .extracting("cnt", "positive", "negative")
                .containsExactly(1, BigDecimal.valueOf(0), BigDecimal.valueOf(-1)));
  }

  @Test
  void 키워드_통계를_바탕으로_키워드_문답별_가중치를_계산한다() {
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(ADVENTURE).score(BigDecimal.valueOf(-1)).build(),
            KeywordScore.builder().keyword(SECURITY).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(SUCCESS).score(BigDecimal.valueOf(1)).build(),
            KeywordScore.builder().keyword(UNIVERSALISM).score(BigDecimal.valueOf(-1)).build());
    KeywordStatistics statistics = new KeywordStatistics(scores);

    Map<Keyword, BigDecimal> actual = statistics.getWeights();

    assertAll(
        () -> then(actual).hasSize(4),
        () -> then(actual.get(ADVENTURE)).isEqualByComparingTo(BigDecimal.valueOf(12.50)),
        () -> then(actual.get(SECURITY)).isEqualByComparingTo(BigDecimal.valueOf(25.00)),
        () -> then(actual.get(SUCCESS)).isEqualByComparingTo(BigDecimal.valueOf(25.00)),
        () -> then(actual.get(UNIVERSALISM)).isEqualByComparingTo(BigDecimal.valueOf(25.00)));
  }
}
