package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScoreNormalizer;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

public class ValueReports {

  private static final ScaledBigDecimal PERCENTAGE100 =
      ScaledBigDecimal.of(BigDecimal.valueOf(100));

  public static List<ValueReport> report(
      final Map<Keyword, BigDecimal> weights,
      final Map<Keyword, KeywordMetrics> metrics,
      final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = getKeywordPercentage(weights, metrics, submissions);

    return percentage.entrySet().stream()
        .map(it -> ValueReport.of(it.getKey(), ScaledBigDecimal.of(it.getValue())))
        .collect(Collectors.toList());
  }

  private static Map<Keyword, BigDecimal> getKeywordPercentage(
      final Map<Keyword, BigDecimal> weightMap,
      final Map<Keyword, KeywordMetrics> metricsMap,
      final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = new HashMap<>();
    Map<Keyword, BigDecimal> sum = getKeywordSum(submissions);

    BigDecimal keywordCnt = BigDecimal.valueOf(weightMap.size());
    BigDecimal sumPerKeyword = PERCENTAGE100.divide(keywordCnt).getValue();

    sum.forEach(
        (k, v) -> {
          var score =
              KeywordScoreNormalizer.normalize(v, metricsMap.get(k)).multiply(weightMap.get(k));

          percentage.put(
              k,
              ScaledBigDecimal.of(score)
                  .divide(sumPerKeyword)
                  .multiply(PERCENTAGE100.getValue())
                  .getValue());
        });
    return percentage;
  }

  private static Map<Keyword, BigDecimal> getKeywordSum(final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> sum = new HashMap<>();

    submissions.forEach(
        submission -> {
          List<KeywordScore> keywordScores = submission.getSelectedOption().getScores();

          keywordScores.forEach(
              keywordScore -> {
                var keyword = keywordScore.getKeyword();
                var score = keywordScore.getScore();
                sum.merge(keyword, ScaledBigDecimal.of(score).getValue(), BigDecimal::add);
              });
        });
    return sum;
  }
}
