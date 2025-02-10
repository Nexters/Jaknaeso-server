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

  public Map<Keyword, KeywordMetrics> getStatistics() {
    Map<Keyword, KeywordMetrics> statistics = new HashMap<>();

    scores.forEach(
        it -> {
          Keyword keyword = it.getKeyword();
          BigDecimal score = it.getScore();

          statistics.computeIfAbsent(keyword, v -> new KeywordMetrics());
          var m = statistics.get(keyword);
          m.update(score);
        });
    return statistics;
  }

  public Map<Keyword, BigDecimal> getKeywordWeights() {
    Map<Keyword, BigDecimal> weights = new HashMap<>();
    Map<Keyword, KeywordMetrics> statistics = getStatistics();

    int keywordCnt = statistics.size();
    BigDecimal sumPerKeyword =
        BigDecimal.valueOf(100).divide(BigDecimal.valueOf(keywordCnt), 2, RoundingMode.DOWN);

    statistics.forEach((k, v) -> weights.put(k, sumPerKeyword.divide(v.getTotal(), 2)));
    return weights;
  }

  @Getter
  public static class KeywordMetrics {

    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal positive = BigDecimal.ZERO;
    private BigDecimal negative = BigDecimal.ZERO;

    public void update(final BigDecimal score) {
      BigDecimal absScore = score.abs();
      total = total.add(absScore);

      if (score.compareTo(BigDecimal.ZERO) > 0) {
        positive = positive.add(absScore);
      } else if (score.compareTo(BigDecimal.ZERO) < 0) {
        negative = negative.add(absScore);
      }
    }
  }
}
