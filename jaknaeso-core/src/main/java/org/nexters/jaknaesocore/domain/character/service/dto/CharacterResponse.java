package org.nexters.jaknaesocore.domain.character.service.dto;

import java.time.LocalDate;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.CharacterType;

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
      final CharacterType characterType,
      final String name,
      final String description,
      final LocalDate startDate,
      final LocalDate endDate) {
    return new CharacterResponse(
        characterId,
        characterNo,
        characterType.getName(),
        name,
        description,
        startDate.toString(),
        endDate.toString());
  }
}
