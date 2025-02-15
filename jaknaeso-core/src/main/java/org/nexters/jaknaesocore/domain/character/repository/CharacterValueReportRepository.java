package org.nexters.jaknaesocore.domain.character.repository;

import java.util.Optional;
import org.nexters.jaknaesocore.domain.character.model.CharacterValueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CharacterValueReportRepository extends JpaRepository<CharacterValueReport, Long> {

  @Query(
      "SELECT c FROM CharacterValueReport c "
          + "JOIN FETCH c.characterRecord cc JOIN FETCH c.valueReports "
          + "WHERE cc.id = :characterId AND c.deletedAt IS NULL")
  Optional<CharacterValueReport> findByCharacterIdAndDeletedAtIsNullWithCharacterAndValueReports(
      final Long characterId);
}
