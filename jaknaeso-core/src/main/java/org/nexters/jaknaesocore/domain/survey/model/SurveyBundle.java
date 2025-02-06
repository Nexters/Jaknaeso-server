package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.common.support.error.CustomException;

@Getter
@NoArgsConstructor
@Entity
public class SurveyBundle extends BaseTimeEntity {

  @OneToMany(mappedBy = "surveyBundle", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Survey> surveys = new ArrayList<>();

  public void addSurvey(final Survey survey) {
    surveys.add(survey);
  }

  public Survey getUnSubmittedSurvey(final List<Survey> submittedSurvey) {
    List<Survey> list =
        surveys.stream().filter(survey -> !submittedSurvey.contains(survey)).toList();

    if (list.isEmpty()) {
      throw CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE;
    }
    int randomNum = ThreadLocalRandom.current().nextInt(list.size());
    return list.get(randomNum);
  }

  public Survey getNextSurvey(final List<Survey> submittedSurvey) {
    List<Survey> list =
        surveys.stream().filter(survey -> !submittedSurvey.contains(survey)).toList();

    if (list.isEmpty()) {
      throw CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE;
    }
    return list.getFirst();
  }

  public boolean isAllSubmitted(final List<SurveySubmission> submissions) {
    return surveys.stream()
        .allMatch(
            survey ->
                submissions.stream()
                    .map(SurveySubmission::getSurvey)
                    .anyMatch(submittedSurvey -> submittedSurvey.equals(survey)));
  }
}
