package org.nexters.jaknaesoserver.domain.survey.controller.dto;

import jakarta.validation.constraints.NotNull;

public record SurveySubmissionRequest(@NotNull Long optionId, String comment) {}
