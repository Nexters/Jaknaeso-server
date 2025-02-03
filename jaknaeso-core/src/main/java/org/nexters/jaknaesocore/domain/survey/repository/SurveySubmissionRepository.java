package org.nexters.jaknaesocore.domain.survey.repository;

import java.util.List;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {

  @EntityGraph(attributePaths = {"survey"})
  List<SurveySubmission> findByMember_IdAndDeletedAtIsNull(final Long memberId);

  Optional<SurveySubmission> findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
      final Long memberId);

  List<SurveySubmission> findByMember_IdAndSurvey_SurveyBundle_Id(
      final Long memberId, final Long bundleId);
}
