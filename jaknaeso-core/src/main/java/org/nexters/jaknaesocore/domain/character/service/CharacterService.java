package org.nexters.jaknaesocore.domain.character.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterValueReport;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRepository;
import org.nexters.jaknaesocore.domain.character.repository.CharacterValueReportRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse.SimpleCharacterResponse;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CharacterService {

  private final MemberRepository memberRepository;
  private final CharacterRepository characterRepository;
  private final CharacterValueReportRepository characterValueReportRepository;

  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(final CharacterCommand command) {
    return characterRepository
        .findByIdAndMemberIdAndDeletedAtIsNullWithMember(command.memberId(), command.characterId())
        .map(
            it ->
                CharacterResponse.builder()
                    .characterNo(it.getCharacterNo())
                    .type(it.getType())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharacterResponse getCurrentCharacter(final Long memberId) {
    return characterRepository
        .findTopByMemberIdAndDeletedAtIsNullWithMember(memberId)
        .map(
            it ->
                CharacterResponse.builder()
                    .characterNo(it.getCharacterNo())
                    .type(it.getType())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharactersResponse getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    final List<SimpleCharacterResponse> characters =
        characterRepository
            .findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(memberId)
            .stream()
            .map(
                it ->
                    SimpleCharacterResponse.builder()
                        .characterNo(it.getCharacterNo())
                        .characterId(it.getId())
                        .bundleId(it.getSurveyBundle().getId())
                        .build())
            .toList();
    return new CharactersResponse(characters);
  }

  @Transactional(readOnly = true)
  public CharacterValueReportResponse getCharacterReport(
      final CharacterValueReportCommand command) {
    characterRepository
        .findByIdAndMemberIdAndDeletedAtIsNullWithMember(command.characterId(), command.memberId())
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);

    final CharacterValueReport report =
        characterValueReportRepository
            .findByCharacterIdAndDeletedAtIsNullWithCharacterAndValueReports(command.characterId())
            .orElseThrow(() -> CustomException.CHARACTER_VALUE_REPORT_NOT_FOUND);
    return CharacterValueReportResponse.of(report.getValueReports());
  }
}
