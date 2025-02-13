package org.nexters.jaknaesocore.domain.character.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

@Getter
public class ValueReports {

  private static final BigDecimal PERCENTAGE100 = BigDecimal.valueOf(100);

  private List<ValueReport> reports;

  private ValueReports(final List<ValueReport> reports) {
    this.reports = reports;
  }

  public static ValueReports of(
      final Map<Keyword, BigDecimal> weights, final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = getKeywordPercentage(weights, submissions);

    List<ValueReport> reports =
        percentage.entrySet().stream()
            .map(it -> ValueReport.of(it.getKey(), it.getValue()))
            .collect(Collectors.toList());
    return new ValueReports(reports);
  }

  private static Map<Keyword, BigDecimal> getKeywordPercentage(
      final Map<Keyword, BigDecimal> weights, final List<SurveySubmission> submissions) {
    Map<Keyword, BigDecimal> percentage = new HashMap<>();
    Map<Keyword, BigDecimal> sum = getKeywordSum(weights, submissions);

    int keywordCnt = weights.size();
    BigDecimal sumPerKeyword =
        PERCENTAGE100.divide(BigDecimal.valueOf(keywordCnt), 2, RoundingMode.DOWN);

    sum.forEach(
        (k, v) ->
            percentage.put(
                k, v.divide(sumPerKeyword, 2, RoundingMode.HALF_UP).multiply(PERCENTAGE100)));
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
