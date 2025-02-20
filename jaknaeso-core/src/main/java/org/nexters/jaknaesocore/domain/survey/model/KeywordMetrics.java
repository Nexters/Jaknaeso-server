package org.nexters.jaknaesocore.domain.survey.model;

import java.util.List;
import java.util.Map;

public record KeywordMetrics(List<OptionScoreMetrics> optionScoreMetrics) {

  public static KeywordMetrics of(final Map<Long, List<KeywordScore>> scoresMap) {
    return new KeywordMetrics(
        scoresMap.entrySet().stream()
            .map(entry -> OptionScoreMetrics.of(entry.getKey(), entry.getValue()))
            .toList());
  }
}
