package org.nexters.jaknaesocore.domain.character.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
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
  private final CharacterRecordRepository characterRecordRepository;

  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(final CharacterCommand command) {
    if (command.bundleId() == null) {
      return getCurrentCharacter(command.memberId());
    }
    return getSpecificCharacter(command.memberId(), command.bundleId());
  }

  private CharacterResponse getCurrentCharacter(final Long memberId) {
    return characterRecordRepository
        .findTopWithMemberByMemberIdAndDeletedAtIsNull(memberId)
        .map(
            it ->
                CharacterResponse.builder()
                    .characterNo(it.getCharacterNo())
                    .characterType(it.getCharacterType())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_RECORD_NOT_FOUND);
  }

  private CharacterResponse getSpecificCharacter(final Long memberId, final Long bundleId) {
    return characterRecordRepository
        .findTopWithMemberByMemberIdAndBundleIdAndDeletedAtIsNull(memberId, bundleId)
        .map(
            it ->
                CharacterResponse.builder()
                    .characterNo(it.getCharacterNo())
                    .characterType(it.getCharacterType())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_RECORD_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharactersResponse getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    final List<SimpleCharacterResponse> characters =
        characterRecordRepository
            .findWithSurveyBundleByMemberIdAndDeletedAtIsNull(memberId)
            .stream()
            .map(
                it ->
                    new SimpleCharacterResponse(it.getCharacterNo(), it.getSurveyBundle().getId()))
            .toList();
    return new CharactersResponse(characters);
  }

  @Transactional(readOnly = true)
  public CharacterValueReportResponse getCharacterReport(
      final CharacterValueReportCommand command) {
    final CharacterRecord characterRecord =
        characterRecordRepository
            .findWithSurveyBundleByMemberIdAndBundleIdAndDeletedAtIsNull(
                command.memberId(), command.bundleId())
            .orElseThrow(() -> CustomException.CHARACTER_RECORD_NOT_FOUND);
    return CharacterValueReportResponse.of(characterRecord.getValueReports());
  }
}
