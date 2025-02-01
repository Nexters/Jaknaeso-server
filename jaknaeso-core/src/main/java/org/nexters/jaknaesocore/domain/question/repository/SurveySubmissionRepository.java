package org.nexters.jaknaesocore.domain.question.repository;

import java.util.Collection;
import org.nexters.jaknaesocore.domain.question.model.SurveySubmission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {

  @EntityGraph(attributePaths = {"survey"})
  Collection<SurveySubmission> findByMember_IdAndDeletedAtIsNull(Long memberId);
}
