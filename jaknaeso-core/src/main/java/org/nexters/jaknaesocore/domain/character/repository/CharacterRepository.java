package org.nexters.jaknaesocore.domain.character.repository;

import java.util.List;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.character.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CharacterRepository extends JpaRepository<Character, Long> {

  @Query(
      "SELECT c FROM Character c JOIN FETCH c.member cm "
          + "WHERE cm.id = :memberId AND c.deletedAt IS NULL ORDER BY c.endDate DESC LIMIT 1")
  Optional<Character> findTopByMemberIdAndDeletedAtIsNullWithMember(final Long memberId);

  @Query(
      "SELECT c FROM Character c JOIN FETCH c.member cm "
          + "WHERE c.id = :id AND cm.id = :memberId AND c.deletedAt IS NULL")
  Optional<Character> findByIdAndMemberIdAndDeletedAtIsNullWithMember(
      final Long id, final Long memberId);

  @Query(
      "SELECT c FROM Character c "
          + "JOIN FETCH c.member cm JOIN FETCH c.surveyBundle "
          + "WHERE cm.id = :memberId AND c.deletedAt IS NULL")
  List<Character> findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(final Long memberId);

  @Query(
      "SELECT c FROM Character c JOIN FETCH c.member cm "
          + "WHERE c.id = :id AND cm.id = :memberId AND c.deletedAt IS NULL")
  Optional<Character> existsByIdAndMemberIdAndDeletedAtIsNullWithMember(
      final Long id, final Long memberId);
}
