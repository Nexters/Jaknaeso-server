package org.nexters.jaknaesocore.domain.character.model;

import java.util.List;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.domain.survey.model.*;

public class ValueReports {
  public static List<ValueReport> report(final List<KeywordScore> scores) {
    return scores.stream()
        .map(it -> ValueReport.of(it.getKeyword(), it.getScore()))
        .collect(Collectors.toList());
  }
}
