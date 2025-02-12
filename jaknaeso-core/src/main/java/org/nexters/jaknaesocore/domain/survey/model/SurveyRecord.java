package org.nexters.jaknaesocore.domain.survey.model;

import lombok.Builder;

@Builder
public record SurveyRecord(
    Long submissionId, String question, String answer, String retrospective, String submittedAt) {

  public static SurveyRecord of(SurveySubmission submission) {
    Long submissionId = submission.getId();
    String question = submission.getSurvey().getContent();
    String answer = submission.getSelectedOption().getContent();
    String retrospective = submission.getRetrospective();
    String submittedAt = submission.getYearMonthDay();
    return new SurveyRecord(submissionId, question, answer, retrospective, submittedAt);
  }
}
