package org.nexters.jaknaesoserver.domain.character.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.character.service.CharacterService;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/characters")
@RestController
public class CharacterController {

  private final CharacterService characterService;

  @GetMapping
  public ApiResponse<CharactersResponse> getCharacters(@RequestParam final Long memberId) {
    CharactersResponse response = characterService.getCharacters(memberId);
    return ApiResponse.success(response);
  }

  @GetMapping("/{characterId}")
  public ApiResponse<CharacterResponse> getCharacter(
      @RequestParam final Long memberId, @PathVariable final Long characterId) {
    CharacterResponse response =
        characterService.getCharacter(new CharacterCommand(memberId, characterId));
    return ApiResponse.success(response);
  }

  @GetMapping("/latest")
  public ApiResponse<CharacterResponse> getCurrentCharacter(@RequestParam final Long memberId) {
    CharacterResponse response = characterService.getCurrentCharacter(memberId);
    return ApiResponse.success(response);
  }

  @GetMapping("/{characterId}/report")
  public ApiResponse<CharacterValueReportResponse> getCharacterReport(
      @RequestParam final Long memberId, @PathVariable final Long characterId) {
    CharacterValueReportResponse response =
        characterService.getCharacterReport(new CharacterValueReportCommand(memberId, characterId));
    return ApiResponse.success(response);
  }
}
