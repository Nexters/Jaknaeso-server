package org.nexters.jaknaesocore.domain.survey.dto;

import lombok.Builder;

@Builder
public record SurveySubmissionHistoryCommand(Long memberId, Long bundleId) {}
