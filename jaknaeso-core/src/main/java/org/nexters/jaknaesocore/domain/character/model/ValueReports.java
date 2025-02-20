package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScoreNormalizer;
import org.nexters.jaknaesocore.domain.survey.model.OptionScoreMetrics;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

public class ValueReports {

  private static final BigDecimal PERCENTAGE100 = BigDecimal.valueOf(100);

  /**
   * @param metrics 가치관별 지표 정보
   * @param submissions 사용자가 제출한 설문 응답 목록
   * @return 가치관 분석 보고서
   */
  public static List<ValueReport> report(
      final Map<Keyword, KeywordMetrics> metrics, final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentages = calculateKeywordPercentages(metrics, submissions);

    return percentages.entrySet().stream()
        .map(it -> ValueReport.of(it.getKey(), ScaledBigDecimal.of(it.getValue())))
        .collect(Collectors.toList());
  }

  /**
   * 각 가치관에 대해 사용자가 얻은 점수를 정규화하고, 퍼센트로 변환
   *
   * @param metrics 가치관별 지표 정보
   * @param submissions 사용자가 제출한 설문 응답 목록
   * @return 각 가치관에 대한 퍼센트 점수 (0-100 범위)
   */
  private static Map<Keyword, BigDecimal> calculateKeywordPercentages(
      final Map<Keyword, KeywordMetrics> metrics, final List<SurveySubmission> submissions) {
    final Map<Keyword, BigDecimal> percentage = new HashMap<>();
    final Map<Keyword, Map<Long, BigDecimal>> sumBySurvey =
        groupScoresByKeywordAndSurvey(submissions);

    sumBySurvey.forEach(
        (keyword, surveyScores) -> {
          final Map<Long, OptionScoreMetrics> surveyOptionMetrics =
              metrics.get(keyword).optionScoreMetrics().stream()
                  .collect(Collectors.toMap(OptionScoreMetrics::surveyId, it -> it));

          final TotalScoreMetrics sumMetrics = sumKeywordScores(surveyScores, surveyOptionMetrics);
          final BigDecimal normalizedScore =
              KeywordScoreNormalizer.normalize(
                  sumMetrics.myTotalScore, sumMetrics.maxTotalScore, sumMetrics.minTotalScore);
          percentage.put(
              keyword, ScaledBigDecimal.of(normalizedScore).multiply(PERCENTAGE100).getValue());
        });
    return percentage;
  }

  /**
   * @param submissions 사용자가 제출한 설문 응답 목록
   * @return 각 가치관에 대한 설문별 점수 합산 결과
   */
  private static Map<Keyword, Map<Long, BigDecimal>> groupScoresByKeywordAndSurvey(
      final List<SurveySubmission> submissions) {
    final Map<Keyword, Map<Long, BigDecimal>> sumBySurvey = new HashMap<>();

    submissions.forEach(
        submission -> {
          final Long surveyId = submission.getSurvey().getId();
          submission
              .getSelectedOption()
              .getScores()
              .forEach(
                  keywordScore -> {
                    var keyword = keywordScore.getKeyword();
                    var score = ScaledBigDecimal.of(keywordScore.getScore()).getValue();

                    sumBySurvey
                        .computeIfAbsent(keyword, k -> new HashMap<>())
                        .merge(surveyId, score, BigDecimal::add);
                  });
        });
    return sumBySurvey;
  }

  /**
   * @param surveyScores 설문별로 사용자가 얻은 가치관 점수
   * @param surveyOptionMetrics 각 설문 선택지에 대한 최대/최소 점수 정보
   * @return 특정 가치관과 관련하여 사용자가 얻은 총 점수, 나올 수 있는 최대/최소 점수
   */
  private static TotalScoreMetrics sumKeywordScores(
      Map<Long, BigDecimal> surveyScores, Map<Long, OptionScoreMetrics> surveyOptionMetrics) {
    BigDecimal myTotalScore = BigDecimal.ZERO;
    BigDecimal maxTotalScore = BigDecimal.ZERO;
    BigDecimal minTotalScore = BigDecimal.ZERO;

    for (var entry : surveyScores.entrySet()) {
      var surveyId = entry.getKey();
      var score = entry.getValue();
      var metrics = surveyOptionMetrics.get(surveyId);

      myTotalScore = myTotalScore.add(score);
      maxTotalScore = maxTotalScore.add(metrics.maxScore());
      minTotalScore = minTotalScore.add(metrics.minScore());
    }
    return new TotalScoreMetrics(myTotalScore, maxTotalScore, minTotalScore);
  }

  private record TotalScoreMetrics(
      BigDecimal myTotalScore, BigDecimal maxTotalScore, BigDecimal minTotalScore) {}
}
