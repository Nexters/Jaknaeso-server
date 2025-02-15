package org.nexters.jaknaesocore.domain.character.service.dto;

import java.time.LocalDate;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;

public record CharacterResponse(
    Long characterId,
    String characterNo,
    String characterType,
    String name,
    String description,
    String startDate,
    String endDate) {

  @Builder
  public static CharacterResponse of(
      final Long characterId,
      final String characterNo,
      final ValueCharacter valueCharacter,
      final String name,
      final String description,
      final LocalDate startDate,
      final LocalDate endDate) {
    return new CharacterResponse(
        characterId,
        characterNo,
        valueCharacter.getKeyword().name(),
        name,
        description,
        startDate.toString(),
        endDate.toString());
  }
}
