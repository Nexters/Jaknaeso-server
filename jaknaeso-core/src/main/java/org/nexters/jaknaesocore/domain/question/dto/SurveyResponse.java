package org.nexters.jaknaesocore.domain.question.dto;

import java.util.List;
import org.nexters.jaknaesocore.domain.question.model.Survey;

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
