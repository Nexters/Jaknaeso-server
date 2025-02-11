package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeywordStatistics {

  private final List<KeywordScore> scores;

  public Map<Keyword, KeywordMetrics> getMetrics() {
    Map<Keyword, KeywordMetrics> metrics = new HashMap<>();

    scores.forEach(
        it -> {
          Keyword keyword = it.getKeyword();
          BigDecimal score = it.getScore();

          metrics.computeIfAbsent(keyword, v -> new KeywordMetrics());
          var m = metrics.get(keyword);
          m.update(score);
        });
    return metrics;
  }

  public Map<Keyword, BigDecimal> getWeights() {
    Map<Keyword, BigDecimal> weights = new HashMap<>();
    Map<Keyword, KeywordMetrics> metrics = getMetrics();

    int keywordCnt = metrics.size();
    BigDecimal sumPerKeyword =
        BigDecimal.valueOf(100).divide(BigDecimal.valueOf(keywordCnt), 2, RoundingMode.DOWN);

    metrics.forEach(
        (k, v) ->
            weights.put(
                k,
                sumPerKeyword.divide(
                    v.getPositive().subtract(v.getNegative()), 2, RoundingMode.HALF_EVEN)));
    return weights;
  }

  @Getter
  public static class KeywordMetrics {

    private int cnt = 0;
    private BigDecimal positive = BigDecimal.ZERO;
    private BigDecimal negative = BigDecimal.ZERO;

    public void update(final BigDecimal score) {
      cnt++;
      if (score.compareTo(BigDecimal.ZERO) > 0) {
        positive = positive.add(score);
      } else if (score.compareTo(BigDecimal.ZERO) < 0) {
        negative = negative.add(score);
      }
    }
  }
}
