package org.nexters.jaknaesocore.domain.survey.dto;

public record SurveyHistoryDetailResponse(Long submissionId) {

  public static SurveyHistoryDetailResponse of(Long submissionId) {
    return new SurveyHistoryDetailResponse(submissionId);
  }
}
