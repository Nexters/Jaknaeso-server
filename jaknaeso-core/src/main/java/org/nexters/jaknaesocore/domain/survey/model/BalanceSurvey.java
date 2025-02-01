package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("BALANCE")
public class BalanceSurvey extends Survey {

  public BalanceSurvey(String content, SurveyBundle surveyBundle) {
    super(content, surveyBundle);
  }

  @Override
  public SurveyType getSurveyType() {
    return SurveyType.BALANCE;
  }
}
