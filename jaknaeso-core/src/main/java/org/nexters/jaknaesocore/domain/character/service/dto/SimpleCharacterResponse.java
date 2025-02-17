package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;

@Builder
public record SimpleCharacterResponse(
    int ordinalNumber, String characterNo, Long characterId, Long bundleId, Boolean isCompleted) {

  public static List<SimpleCharacterResponse> listOf(final List<CharacterRecord> records) {
    return records.stream().map(SimpleCharacterResponse::of).toList();
  }

  public static SimpleCharacterResponse of(final CharacterRecord record) {
    return SimpleCharacterResponse.builder()
        .ordinalNumber(record.getOrdinalNumber())
        .characterNo(record.getCharacterNo())
        .characterId(record.getId())
        .bundleId(record.getSurveyBundle().getId())
        .isCompleted(true)
        .build();
  }
}
