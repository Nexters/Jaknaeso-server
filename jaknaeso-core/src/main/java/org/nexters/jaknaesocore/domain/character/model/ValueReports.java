package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScoreNormalizer;
import org.nexters.jaknaesocore.domain.survey.model.KeywordStatistics.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

@Getter
public class ValueReports {

  private static final ScaledBigDecimal PERCENTAGE100 =
      ScaledBigDecimal.of(BigDecimal.valueOf(100));

  private List<ValueReport> reports;

  private ValueReports(final List<ValueReport> reports) {
    this.reports = reports;
  }

  public static ValueReports of(
      final Map<Keyword, BigDecimal> weights,
      final Map<Keyword, KeywordMetrics> metrics,
      final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = getKeywordPercentage(weights, metrics, submissions);

    List<ValueReport> reports =
        percentage.entrySet().stream()
            .map(it -> ValueReport.of(it.getKey(), ScaledBigDecimal.of(it.getValue())))
            .collect(Collectors.toList());
    return new ValueReports(reports);
  }

  private static Map<Keyword, BigDecimal> getKeywordPercentage(
      final Map<Keyword, BigDecimal> weights,
      final Map<Keyword, KeywordMetrics> metrics,
      final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = new HashMap<>();
    Map<Keyword, BigDecimal> sum = getKeywordSum(weights, submissions);

    BigDecimal keywordCnt = BigDecimal.valueOf(weights.size());
    BigDecimal sumPerKeyword = PERCENTAGE100.divide(keywordCnt).getValue();

    sum.forEach(
        (k, v) -> {
          var normalizer = new KeywordScoreNormalizer(weights.get(k), metrics.get(k));
          var normalizedScore = normalizer.normalize(v);
          percentage.put(
              k,
              ScaledBigDecimal.of(normalizedScore)
                  .divide(sumPerKeyword)
                  .multiply(PERCENTAGE100.getValue())
                  .getValue());
        });
    return percentage;
  }

  private static Map<Keyword, BigDecimal> getKeywordSum(
      final Map<Keyword, BigDecimal> weights, final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> sum = new HashMap<>();

    submissions.forEach(
        submission -> {
          List<KeywordScore> keywordScores = submission.getSelectedOption().getScores();

          keywordScores.forEach(
              keywordScore -> {
                var keyword = keywordScore.getKeyword();
                var score = keywordScore.getScore();
                var weight = weights.get(keyword);

                sum.merge(keyword, score.multiply(weight), BigDecimal::add);
              });
        });
    return sum;
  }
}
