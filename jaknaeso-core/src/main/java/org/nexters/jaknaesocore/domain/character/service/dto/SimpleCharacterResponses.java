package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;

public class SimpleCharacterResponses {

  private List<SimpleCharacterResponse> responses;

  public SimpleCharacterResponses(final Long latestBundleId, final List<CharacterRecord> records) {
    this.responses = records.stream().map(this::toCompletedResponse).collect(Collectors.toList());

    if (isIncompleteCharacter(latestBundleId, records)) {
      this.responses.add(toIncompleteResponse(latestBundleId));
    }
  }

  private boolean isIncompleteCharacter(final Long bundleId, final List<CharacterRecord> records) {
    return bundleId != null
        && records.stream()
            .noneMatch(record -> Objects.equals(record.getSurveyBundle().getId(), bundleId));
  }

  private SimpleCharacterResponse toCompletedResponse(final CharacterRecord record) {
    return SimpleCharacterResponse.builder()
        .characterNo(record.getCharacterNo())
        .characterId(record.getId())
        .bundleId(record.getSurveyBundle().getId())
        .isCompleted(true)
        .build();
  }

  private SimpleCharacterResponse toIncompleteResponse(final Long bundleId) {
    return SimpleCharacterResponse.builder()
        .characterNo("TODO")
        .characterId(null)
        .bundleId(bundleId)
        .isCompleted(false)
        .build();
  }

  public List<SimpleCharacterResponse> getResponses() {
    return responses;
  }
}
