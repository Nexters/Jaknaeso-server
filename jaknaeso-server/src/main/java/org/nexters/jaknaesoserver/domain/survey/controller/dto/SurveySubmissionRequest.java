package org.nexters.jaknaesoserver.domain.survey.controller.dto;

import jakarta.validation.constraints.NotNull;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionCommand;

public record SurveySubmissionRequest(@NotNull Long optionId, String comment) {
  public SurveySubmissionCommand toServiceRequest() {
    return new SurveySubmissionCommand(optionId(), comment());
  }
}
