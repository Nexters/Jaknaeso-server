package org.nexters.jaknaesocore.domain.survey.model;

import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record SurveyRecord(
    String question, String answer, String retrospective, String submittedAt) {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy.MM.dd");

  public static SurveyRecord of(SurveySubmission submission) {
    String question = submission.getSurvey().getContent();
    String answer = submission.getSelectedOption().getContent();
    String retrospective = submission.getRetrospective();
    String submittedAt = submission.getSubmittedAt().format(DATE_TIME_FORMATTER);
    return new SurveyRecord(question, answer, retrospective, submittedAt);
  }
}
