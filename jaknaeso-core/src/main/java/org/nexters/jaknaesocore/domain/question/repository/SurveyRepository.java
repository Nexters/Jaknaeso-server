package org.nexters.jaknaesocore.domain.question.repository;

import org.nexters.jaknaesocore.domain.question.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {}
