package org.nexters.jaknaesocore.domain.character.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

public class ValueCharacters {

  private final Map<Keyword, ValueCharacter> values;

  private ValueCharacters(final Map<Keyword, ValueCharacter> values) {
    this.values = values;
  }

  public static ValueCharacters of(final List<ValueCharacter> values) {
    return new ValueCharacters(
        values.stream()
            .collect(
                Collectors.toMap(ValueCharacter::getKeyword, valueCharacter -> valueCharacter)));
  }

  public ValueCharacter findTopValueCharacter(final List<ValueReport> valueReports) {
    final ValueReport topReport =
        valueReports.stream()
            .max(Comparator.comparing(ValueReport::getPercentage))
            .orElseThrow(() -> new IllegalStateException("ValueReport가 비어 있습니다."));

    return values.get(topReport.getKeyword());
  }
}
