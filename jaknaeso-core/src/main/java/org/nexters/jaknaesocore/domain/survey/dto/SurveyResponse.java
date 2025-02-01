package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;
import org.nexters.jaknaesocore.domain.survey.model.Survey;

public record SurveyResponse(
    Long id, String contents, String surveyType, List<SurveyOptionsResponse> options) {

  public static SurveyResponse of(Survey survey) {
    return new SurveyResponse(
        survey.getId(),
        survey.getContent(),
        survey.getSurveyType().name(),
        SurveyOptionsResponse.of(survey.getOptions()));
  }
}
