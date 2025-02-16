package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.ArrayList;
import java.util.List;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;

public class SimpleCharacterResponses {

  private final List<SimpleCharacterResponse> responses = new ArrayList<>();

  public void addCompleteResponse(final CharacterRecord record) {
    responses.add(
        SimpleCharacterResponse.builder()
            .characterNo(record.getCharacterNo())
            .characterId(record.getId())
            .bundleId(record.getSurveyBundle().getId())
            .isCompleted(true)
            .build());
  }

  public void addIncompleteResponse(final Long bundleId) {
    responses.add(
        SimpleCharacterResponse.builder()
            .characterNo("TODO")
            .characterId(null)
            .bundleId(bundleId)
            .isCompleted(false)
            .build());
  }

  public List<SimpleCharacterResponse> getResponses() {
    return responses;
  }
}
