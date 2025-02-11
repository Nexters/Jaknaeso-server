package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ValueReport {

  private static final BigDecimal PERCENTAGE100 = BigDecimal.valueOf(100);

  private Map<Keyword, BigDecimal> percentage;

  private ValueReport(final Map<Keyword, BigDecimal> percentage) {
    this.percentage = percentage;
  }

  public static ValueReport of(
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
    return new ValueReport(percentage);
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
