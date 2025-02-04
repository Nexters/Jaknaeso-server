package org.nexters.jaknaesoserver.domain.survey.controller.dto;

import jakarta.validation.constraints.NotNull;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionServiceRequest;

public record SurveySubmissionRequest(@NotNull Long optionId, String comment) {
  public SurveySubmissionServiceRequest toServiceRequest() {
    return new SurveySubmissionServiceRequest(optionId(), comment());
  }
}
