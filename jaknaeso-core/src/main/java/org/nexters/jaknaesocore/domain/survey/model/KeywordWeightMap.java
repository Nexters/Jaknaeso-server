package org.nexters.jaknaesocore.domain.survey.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;

public class KeywordWeightMap {

  public static Map<Keyword, BigDecimal> generate(final Map<Keyword, KeywordMetrics> metricsMap) {
    Map<Keyword, BigDecimal> weightMap = new HashMap<>();

    int keywordCnt = metricsMap.size();
    ScaledBigDecimal sumPerKeyword =
        ScaledBigDecimal.of(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(keywordCnt));

    metricsMap.forEach(
        (k, v) -> {
          var sum = v.getPositive().subtract(v.getNegative());
          weightMap.put(k, sumPerKeyword.divide(sum).getValue());
        });
    return weightMap;
  }
}
