package org.nexters.jaknaesocore.domain.survey.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeywordMetricsMap {

  public static Map<Keyword, KeywordMetrics> generate(final List<KeywordScore> scores) {
    return scores.stream()
        .collect(Collectors.groupingBy(KeywordScore::getKeyword, Collectors.toList()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> KeywordMetrics.create(entry.getValue())));
  }
}
