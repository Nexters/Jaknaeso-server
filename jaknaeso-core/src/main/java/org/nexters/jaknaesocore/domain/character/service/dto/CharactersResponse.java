package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;
import lombok.Builder;

public record CharactersResponse(List<SimpleCharacterResponse> characters) {

  @Builder
  public record SimpleCharacterResponse(String characterNo, Long characterId, Long bundleId) {}
}
