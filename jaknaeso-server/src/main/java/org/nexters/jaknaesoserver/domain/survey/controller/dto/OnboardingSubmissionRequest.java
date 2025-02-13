package org.nexters.jaknaesoserver.domain.survey.controller.dto;

import java.util.List;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionResult;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionsCommand;

public record OnboardingSubmissionRequest(List<OnboardingSubmissionInfoRequest> submissionsInfo) {
  public OnboardingSubmissionsCommand toCommand(Long memberId) {
    return new OnboardingSubmissionsCommand(
        submissionsInfo.stream()
            .map(
                submissionInfo ->
                    new OnboardingSubmissionResult(
                        submissionInfo.surveyId(), submissionInfo.optionId()))
            .toList(),
        memberId);
  }
}
