package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ONBOARDING")
public class OnboardingSurvey extends Survey {

  public OnboardingSurvey(final String content, final SurveyBundle surveyBundle) {
    super(content, surveyBundle);
  }

  @Override
  public SurveyType getSurveyType() {
    return SurveyType.ONBOARDING;
  }
}
