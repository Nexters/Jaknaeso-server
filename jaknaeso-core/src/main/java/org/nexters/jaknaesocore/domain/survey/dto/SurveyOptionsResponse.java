package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;

public record SurveyOptionsResponse(Long id, String optionContents) {
  public static List<SurveyOptionsResponse> of(List<SurveyOption> options) {
    return options.stream()
        .map(option -> new SurveyOptionsResponse(option.getId(), option.getContent()))
        .toList();
  }
}
