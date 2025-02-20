package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.groups.Tuple.*;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KeywordScoresTest {

  @DisplayName("키워드 점수를 정규화하여 0~100 스케일로 조정한다")
  @Test
  void keywordScoreScalePercent() {
    // given
    KeywordScore keywordScore1 =
        KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.valueOf(1)).build();
    KeywordScore keywordScore2 =
        KeywordScore.builder().keyword(Keyword.SELF_DIRECTION).score(BigDecimal.valueOf(2)).build();
    KeywordScore keywordScore3 =
        KeywordScore.builder().keyword(Keyword.STABILITY).score(BigDecimal.valueOf(3)).build();
    // when
    List<KeywordScore> scores =
        KeywordScores.percentScale(List.of(keywordScore1, keywordScore2, keywordScore3));
    // then
    BDDAssertions.then(scores)
        .extracting("keyword", "score")
        .containsExactlyInAnyOrder(
            tuple(Keyword.BENEVOLENCE, BigDecimal.valueOf(900, 2)),
            tuple(Keyword.SELF_DIRECTION, BigDecimal.valueOf(5500, 2)),
            tuple(Keyword.STABILITY, BigDecimal.valueOf(10000, 2)));
  }
}
