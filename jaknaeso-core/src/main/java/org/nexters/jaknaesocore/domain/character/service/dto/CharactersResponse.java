package org.nexters.jaknaesocore.domain.character.service.dto;

import java.util.List;

public record CharactersResponse(List<CharacterResponse> characters) {

  public record CharacterResponse(String characterNo, Long bundleId) {}
}
