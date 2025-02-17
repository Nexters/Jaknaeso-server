package org.nexters.jaknaesocore.domain.survey.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.domain.survey.model.OnboardingSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingSurveyRepository extends JpaRepository<OnboardingSurvey, Long> {

  Optional<OnboardingSurvey> findTopBy();
}
