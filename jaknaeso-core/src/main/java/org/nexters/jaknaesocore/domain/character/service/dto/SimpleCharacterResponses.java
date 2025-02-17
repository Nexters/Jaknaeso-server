package org.nexters.jaknaesocore.domain.character.service.dto;

import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;

public class SimpleCharacterResponses {

  public static SimpleCharacterResponse of(final CharacterRecord record) {
    return SimpleCharacterResponse.builder()
        .characterNo(record.getCharacterNo())
        .characterId(record.getId())
        .bundleId(record.getSurveyBundle().getId())
        .isCompleted(true)
        .build();
  }
}
