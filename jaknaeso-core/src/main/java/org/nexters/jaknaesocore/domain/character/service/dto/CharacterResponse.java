package org.nexters.jaknaesocore.domain.character.service.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.CharacterTrait;
import org.nexters.jaknaesocore.domain.character.model.CharacterTraitType;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@Builder
public record CharacterResponse(
    Long characterId,
    Long bundleId,
    String characterNo,
    Keyword characterType,
    String name,
    String description,
    List<CharacterTraitResponse> mainTraits,
    List<CharacterTraitResponse> strengths,
    List<CharacterTraitResponse> weaknesses,
    String startDate,
    String endDate) {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy.MM.dd");

  @Builder
  public static CharacterResponse of(final CharacterRecord characterRecord) {
    return new CharacterResponse(
        characterRecord.getId(),
        characterRecord.getSurveyBundle().getId(),
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
        characterRecord.getStartDate().format(DATE_TIME_FORMATTER),
        characterRecord.getEndDate().format(DATE_TIME_FORMATTER));
  }

  @Builder
  public record CharacterTraitResponse(String description) {
    public static CharacterTraitResponse of(final CharacterTrait characterTrait) {
      return new CharacterTraitResponse(characterTrait.getDescription());
    }
  }
}
