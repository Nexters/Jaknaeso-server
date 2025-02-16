package org.nexters.jaknaesocore.domain.character.model;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CharacterRecords {

  private final List<CharacterRecord> records;

  public boolean isIncompleteCharacter(final Long bundleId) {
    if (bundleId == null) {
      return false;
    }
    return records.stream().noneMatch(record -> record.getSurveyBundle().getId().equals(bundleId));
  }
}
