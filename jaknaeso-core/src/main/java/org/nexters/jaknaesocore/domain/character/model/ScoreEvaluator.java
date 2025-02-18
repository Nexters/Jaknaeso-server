package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordMetrics;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

public class ScoreEvaluator {

  private final List<KeywordScore> scores;
  private final List<SurveySubmission> submissions;

  @Builder
  private ScoreEvaluator(
      final List<KeywordScore> scores, final List<SurveySubmission> submissions) {
    this.scores = scores;
    this.submissions = submissions;
  }

  public static ScoreEvaluator of(
      final List<KeywordScore> scores, final List<SurveySubmission> submissions) {
    return new ScoreEvaluator(scores, submissions);
  }

  public List<ValueReport> generateValueReports() {
    final List<ValueReport> valueReports = provideValueReport();
    return valueReports;
  }

  private List<ValueReport> provideValueReport() {
    final Map<Keyword, KeywordMetrics> metricsMap = calculateKeywordMetrics(scores);
    final Map<Keyword, BigDecimal> weightMap = calculateKeywordWeights(metricsMap);

    return ValueReports.report(weightMap, metricsMap, submissions);
  }

  private Map<Keyword, KeywordMetrics> calculateKeywordMetrics(final List<KeywordScore> scores) {
    return scores.stream()
        .collect(Collectors.groupingBy(KeywordScore::getKeyword, Collectors.toList()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> KeywordMetrics.create(entry.getValue())));
  }

  private Map<Keyword, BigDecimal> calculateKeywordWeights(
      final Map<Keyword, KeywordMetrics> metricsMap) {
    int keywordCnt = metricsMap.size();
    ScaledBigDecimal sumPerKeyword = ScaledBigDecimal.of(BigDecimal.valueOf(100));

    Map<Keyword, BigDecimal> weightMap = new HashMap<>();
    metricsMap.forEach(
        (k, v) -> {
          var sum = v.getPositive().subtract(v.getNegative());
          weightMap.put(k, sumPerKeyword.divide(sum).getValue());
        });
    return weightMap;
  }
}
