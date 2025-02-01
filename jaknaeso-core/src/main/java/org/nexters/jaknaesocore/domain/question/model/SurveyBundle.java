package org.nexters.jaknaesocore.domain.question.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

  public void addSurvey(Survey survey) {
    surveys.add(survey);
  }

  public Survey getUnSubmittedSurvey(Set<Long> submittedSurveyIds) {
    List<Survey> list =
        surveys.stream().filter(survey -> !submittedSurveyIds.contains(survey.getId())).toList();

    if (list.isEmpty()) {
      throw new IllegalStateException("모든 설문을 완료하셨습니다.");
    }
    int randomNum = ThreadLocalRandom.current().nextInt(list.size());
    return list.get(randomNum);
  }
}
