package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;

public record SurveyHistoryResponse(
    Long bundleId,
    List<SurveyHistoryDetailResponse> surveyHistoryDetails,
    Integer nextSurveyIndex,
    boolean isCompleted) {

  public static SurveyHistoryResponse of(
      Long bundleId,
      List<SurveyHistoryDetailResponse> surveyHistoryDetails,
      Integer nextSurveyIndex,
      boolean isCompleted) {
    return new SurveyHistoryResponse(bundleId, surveyHistoryDetails, nextSurveyIndex, isCompleted);
  }

  public static SurveyHistoryResponse createNextBundleSurveyHistory(Long bundleId) {
    return new SurveyHistoryResponse(bundleId, List.of(), 1, false);
  }
}
