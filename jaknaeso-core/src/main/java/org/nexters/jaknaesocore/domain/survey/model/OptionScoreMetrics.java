package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @param surveyId 설문 아이디
 * @param maxScore 해당 설문 선택지에서 특정 가치관에 대해 얻을 수 있는 최대 점수
 * @param minScore 해당 설문 선택지에서 특정 가치관에 대해 얻을 수 있는 최소 점수
 */
public record OptionScoreMetrics(Long surveyId, BigDecimal maxScore, BigDecimal minScore) {

  /**
   * @param surveyId 설문 아이디
   * @param optionScores 해당 설문 선택지의 특정 가치관에 부여된 점수 목록
   */
  public static OptionScoreMetrics of(final Long surveyId, final List<KeywordScore> optionScores) {
    if (optionScores.size() == 1) {
      var score = optionScores.getFirst().getScore();
      return score.compareTo(BigDecimal.ZERO) >= 0
          ? new OptionScoreMetrics(surveyId, score, BigDecimal.ZERO)
          : new OptionScoreMetrics(surveyId, BigDecimal.ZERO, score);
    }
    var maxScore = optionScores.getFirst().getScore();
    var minScore = optionScores.getLast().getScore();
    return new OptionScoreMetrics(surveyId, maxScore, minScore);
  }
}
