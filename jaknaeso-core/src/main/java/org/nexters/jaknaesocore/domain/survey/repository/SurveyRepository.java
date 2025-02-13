package org.nexters.jaknaesocore.domain.survey.repository;

import java.util.List;
import org.nexters.jaknaesocore.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

  @Query("SELECT s FROM Survey s JOIN FETCH s.options WHERE s.id IN :surveyIds")
  List<Survey> findAllByIdWithOptions(List<Long> surveyIds);
}
