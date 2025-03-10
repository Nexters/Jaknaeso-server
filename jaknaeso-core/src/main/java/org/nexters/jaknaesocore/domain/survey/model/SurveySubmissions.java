package org.nexters.jaknaesocore.domain.survey.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SurveySubmissions {

  private final List<SurveySubmission> submissions;

  public static SurveySubmissions of(List<SurveySubmission> submissions) {
    return new SurveySubmissions(submissions);
  }

  public List<Survey> getSubmittedSurvey(final Long memberId) {
    return submissions.stream()
        .filter(submission -> submission.getMember().getId().equals(memberId))
        .map(SurveySubmission::getSurvey)
        .collect(Collectors.toList());
  }

  public boolean isFirstSubmitted() {
    return submissions.size() == 1;
  }
}
