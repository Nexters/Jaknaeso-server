package org.nexters.jaknaesocore.domain.survey.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeywordScores {

  @Getter private final List<KeywordScore> values;

  public static KeywordScores of(List<Survey> values) {
    return new KeywordScores(
        values.stream()
            .flatMap(survey -> survey.getOptions().stream())
            .flatMap(option -> option.getScores().stream())
            .toList());
  }
}
