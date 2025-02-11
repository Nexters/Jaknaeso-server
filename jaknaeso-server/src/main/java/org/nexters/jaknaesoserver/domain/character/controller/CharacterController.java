package org.nexters.jaknaesoserver.domain.character.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.character.service.CharacterService;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/characters")
@RestController
public class CharacterController {

  private final CharacterService characterService;

  @GetMapping
  public ApiResponse<CharactersResponse> getCharacters(@RequestParam Long memberId) {
    CharactersResponse response = characterService.getCharacters(memberId);
    return ApiResponse.success(response);
  }
}
