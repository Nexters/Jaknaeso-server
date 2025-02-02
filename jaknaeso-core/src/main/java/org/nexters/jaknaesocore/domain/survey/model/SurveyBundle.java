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
      throw new IllegalStateException("모든 설문을 완료하셨습니다.");
    }
    int randomNum = ThreadLocalRandom.current().nextInt(list.size());
    return list.get(randomNum);
  }
}
