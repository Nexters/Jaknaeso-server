package org.nexters.jaknaesocore.domain.survey.dto;

import lombok.Builder;

@Builder
public record SurveySubmissionCommand(
    Long optionId, Long surveyId, Long memberId, String comment) {}
