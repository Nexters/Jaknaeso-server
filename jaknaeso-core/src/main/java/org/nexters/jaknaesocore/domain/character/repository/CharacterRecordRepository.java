package org.nexters.jaknaesocore.domain.character.repository;

import java.util.List;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CharacterRecordRepository extends JpaRepository<CharacterRecord, Long> {

  @Query(
      "SELECT c FROM CharacterRecord c JOIN FETCH c.member cm "
          + "WHERE cm.id = :memberId AND c.deletedAt IS NULL ORDER BY c.endDate DESC LIMIT 1")
  Optional<CharacterRecord> findTopWithMemberByMemberIdAndDeletedAtIsNull(final Long memberId);

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "JOIN FETCH c.member cm JOIN FETCH c.surveyBundle cs "
          + "WHERE cm.id = :memberId AND cs.id = :bundleId AND c.deletedAt IS NULL")
  Optional<CharacterRecord> findTopWithMemberByMemberIdAndBundleIdAndDeletedAtIsNull(
      final Long memberId, final Long bundleId);

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "JOIN FETCH c.member cm JOIN FETCH c.surveyBundle cs JOIN FETCH c.valueReports "
          + "WHERE cm.id = :memberId AND cs.id = :bundleId AND c.deletedAt IS NULL")
  Optional<CharacterRecord> findWithSurveyBundleByMemberIdAndBundleIdAndDeletedAtIsNull(
      final Long memberId, final Long bundleId);

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "JOIN FETCH c.member cm JOIN FETCH c.surveyBundle "
          + "WHERE cm.id = :memberId AND c.deletedAt IS NULL")
  List<CharacterRecord> findWithSurveyBundleByMemberIdAndDeletedAtIsNull(final Long memberId);
}
