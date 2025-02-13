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
      final String type,
      final String description,
      final LocalDate startDate,
      final LocalDate endDate) {
    return new CharacterResponse(
        characterNo, type, description, startDate.toString(), endDate.toString());
  }
}
