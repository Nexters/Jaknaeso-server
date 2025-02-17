package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.CharacterTrait;
import org.nexters.jaknaesocore.domain.character.model.CharacterTraitType;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@Builder
public record CharacterResponse(
    Long characterId,
    String characterNo,
    Keyword characterType,
    String name,
    String description,
    List<CharacterTraitResponse> mainTraits,
    List<CharacterTraitResponse> strengths,
    List<CharacterTraitResponse> weaknesses,
    String startDate,
    String endDate) {

  @Builder
  public static CharacterResponse of(final CharacterRecord characterRecord) {
    return new CharacterResponse(
        characterRecord.getId(),
        characterRecord.getCharacterNo(),
        characterRecord.getKeyword(),
        characterRecord.getName(),
        characterRecord.getDescription(),
        characterRecord.getTraitsByType(CharacterTraitType.MAIN_TRAIT).stream()
            .map(CharacterTraitResponse::of)
            .toList(),
        characterRecord.getTraitsByType(CharacterTraitType.STRENGTH).stream()
            .map(CharacterTraitResponse::of)
            .toList(),
        characterRecord.getTraitsByType(CharacterTraitType.WEAKNESS).stream()
            .map(CharacterTraitResponse::of)
            .toList(),
        characterRecord.getStartDate().toString(),
        characterRecord.getEndDate().toString());
  }

  @Builder
  public record CharacterTraitResponse(String description) {

    public static CharacterTraitResponse of(final CharacterTrait characterTrait) {
      return new CharacterTraitResponse(characterTrait.getDescription());
    }
  }
}
