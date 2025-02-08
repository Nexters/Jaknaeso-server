package org.nexters.jaknaesocore.domain.survey.model;

import lombok.Builder;

@Builder
public record SurveyRecord(
    String question, String answer, String retrospective, String submittedAt) {

  public static SurveyRecord of(SurveySubmission submission) {
    String question = submission.getSurvey().getContent();
    String answer = submission.getSelectedOption().getContent();
    String retrospective = submission.getRetrospective();
    String submittedAt = submission.getYearMonthDay();
    return new SurveyRecord(question, answer, retrospective, submittedAt);
  }
}
