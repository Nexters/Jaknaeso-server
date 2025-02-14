package org.nexters.jaknaesocore.domain.survey.dto;

public record SurveyHistoryDetailResponse(Long submissionId, int index) {

  public static SurveyHistoryDetailResponse of(Long submissionId, int index) {
    return new SurveyHistoryDetailResponse(submissionId, index);
  }
}
