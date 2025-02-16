package org.nexters.jaknaesocore.domain.character.service.dto;

import lombok.Builder;

@Builder
public record SimpleCharacterResponse(
    String characterNo, Long characterId, Long bundleId, Boolean isCompleted) {}
