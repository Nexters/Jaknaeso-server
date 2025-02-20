package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;

class ValueReportsTest {

  @Test
  void 키워드_점수를_통해_리포트를_생성한다() {
    // given
    List<KeywordScore> keywordScores =
        List.of(
            KeywordScore.of(Keyword.ADVENTURE, BigDecimal.ONE),
            KeywordScore.of(Keyword.SELF_DIRECTION, BigDecimal.TEN));
    // when
    List<ValueReport> report = ValueReports.report(keywordScores);
    // then
    BDDAssertions.then(report)
        .hasSize(2)
        .extracting("keyword", "percentage")
        .containsExactly(
            BDDAssertions.tuple(Keyword.ADVENTURE, BigDecimal.valueOf(100, 2)),
            BDDAssertions.tuple(Keyword.SELF_DIRECTION, BigDecimal.valueOf(1000, 2)));
  }
}
