package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;

public record SurveyHistoryResponse(
    Long bundleId,
    List<SurveyHistoryDetailResponse> surveyHistoryDetails,
    Integer nextSurveyIndex) {}
