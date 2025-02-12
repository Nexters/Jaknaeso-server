package org.nexters.jaknaesocore.domain.survey.repository;

import java.util.List;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {

  @EntityGraph(attributePaths = {"survey"})
  List<SurveySubmission> findByMember_IdAndDeletedAtIsNull(final Long memberId);

  Optional<SurveySubmission> findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
      final Long memberId);

  List<SurveySubmission> findByMember_IdAndSurvey_SurveyBundle_Id(
      final Long memberId, final Long bundleId);

  @Query(
      "SELECT s FROM SurveySubmission s "
          + "JOIN FETCH s.member sm JOIN FETCH s.survey ss JOIN FETCH ss.surveyBundle sb "
          + "WHERE sm.id = :memberId AND sb.id = :bundleId AND s.deletedAt IS NULL")
  List<SurveySubmission> findWithSurveyByMemberIdAndBundleIdAndDeletedAtIsNull(
      final Long memberId, final Long bundleId);

  @Query(
      "SELECT s FROM SurveySubmission s "
          + "JOIN FETCH s.member sm JOIN FETCH s.survey ss JOIN FETCH ss.surveyBundle sb "
          + "WHERE sm.id = :memberId AND s.deletedAt IS NULL")
  List<SurveySubmission> findWithSurveyBundlesByMemberIdAndDeletedAtIsNull(final Long memberId);
}
