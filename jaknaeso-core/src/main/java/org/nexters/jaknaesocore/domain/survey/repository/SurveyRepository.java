package org.nexters.jaknaesocore.domain.survey.repository;

import org.nexters.jaknaesocore.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {}
