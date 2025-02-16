package org.nexters.jaknaesocore.domain.survey.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeywordScores {

  private final List<Survey> surveys;

  public List<KeywordScore> getKeywordScores() {
    return surveys.stream()
        .flatMap(survey -> survey.getOptions().stream())
        .flatMap(option -> option.getScores().stream())
        .collect(Collectors.toList());
  }
}
