package org.nexters.jaknaesocore.domain.character.repository;

import java.util.List;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CharacterRecordRepository extends JpaRepository<CharacterRecord, Long> {

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "JOIN FETCH c.member cm "
          + "LEFT JOIN c.valueCharacter vc "
          + "WHERE cm.id = :memberId "
          + "AND vc.id IS NOT NULL "
          + "AND c.deletedAt IS NULL "
          + "ORDER BY c.endDate DESC LIMIT 1")
  Optional<CharacterRecord> findLatestCompletedCharacter(final Long memberId);

  @Query(
      "SELECT c FROM CharacterRecord c JOIN FETCH c.member cm "
          + "WHERE c.id = :id AND cm.id = :memberId AND c.deletedAt IS NULL")
  Optional<CharacterRecord> findByIdAndMemberIdAndDeletedAtIsNullWithMember(
      final Long id, final Long memberId);

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "JOIN FETCH c.member cm JOIN FETCH c.surveyBundle "
          + "WHERE cm.id = :memberId AND c.deletedAt IS NULL")
  List<CharacterRecord> findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(
      final Long memberId);

  @Query(
      "SELECT c FROM CharacterRecord c JOIN FETCH c.valueReports cr "
          + "WHERE c.id = :id AND c.deletedAt IS NULL")
  Optional<CharacterRecord> findByIdAndDeletedAtIsNullWithValueReports(final Long id);

  @Query(
      "SELECT c FROM CharacterRecord c "
          + "WHERE c.member.id = :memberId "
          + "AND c.surveyBundle.id = :bundleId "
          + "AND c.deletedAt IS NULL")
  Optional<CharacterRecord> findByMemberIdAndBundleId(final Long memberId, final Long bundleId);
}
