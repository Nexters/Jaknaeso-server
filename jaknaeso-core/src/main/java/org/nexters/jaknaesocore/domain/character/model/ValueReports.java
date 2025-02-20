package org.nexters.jaknaesocore.domain.character.model;

import java.util.List;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.*;

public class ValueReports {

  private final List<ValueReport> values;

  private ValueReports(List<ValueReport> valueReports) {
    this.values = valueReports;
  }

  public static List<ValueReport> report(final List<KeywordScore> scores) {
    return scores.stream()
        .map(it -> ValueReport.of(it.getKeyword(), ScaledBigDecimal.of(it.getScore())))
        .collect(Collectors.toList());
  }
}
