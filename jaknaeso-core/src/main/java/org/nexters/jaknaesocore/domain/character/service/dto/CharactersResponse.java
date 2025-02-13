package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;

public record CharactersResponse(List<SimpleCharacterResponse> characters) {

  public record SimpleCharacterResponse(String characterNo, Long bundleId) {}
}
