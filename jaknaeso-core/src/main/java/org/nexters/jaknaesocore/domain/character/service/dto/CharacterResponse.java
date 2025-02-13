package org.nexters.jaknaesocore.domain.character.service.dto;

import java.time.LocalDate;
import lombok.Builder;

public record CharacterResponse(
    String characterNo,
    String characterType,
    String description,
    String startDate,
    String endDate) {

  @Builder
  public static CharacterResponse of(
      final String characterNo,
      final String characterType,
      final LocalDate startDate,
      final LocalDate endDate) {
    return new CharacterResponse(
        characterNo, characterType, "TODO: description", startDate.toString(), endDate.toString());
  }
}
