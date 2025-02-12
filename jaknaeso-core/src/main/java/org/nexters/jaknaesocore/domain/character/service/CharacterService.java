package org.nexters.jaknaesocore.domain.character.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse.CharacterResponse;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CharacterService {

  private final MemberRepository memberRepository;
  private final CharacterRecordRepository characterRecordRepository;

  @Transactional(readOnly = true)
  public CharactersResponse getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    final List<CharacterResponse> characters =
        characterRecordRepository
            .findWithSurveyBundleByMemberIdAndDeletedAtIsNull(memberId)
            .stream()
            .map(it -> new CharacterResponse(it.getCharacterNo(), it.getSurveyBundle().getId()))
            .toList();
    return new CharactersResponse(characters);
  }

  @Transactional(readOnly = true)
  public CharacterReportResponse getCharacterReport(final CharacterReportCommand command) {
    final CharacterRecord characterRecord =
        characterRecordRepository
            .findWithSurveyBundleByMemberIdAndBundleIdAndDeletedAtIsNull(
                command.memberId(), command.bundleId())
            .orElseThrow(() -> CustomException.CHARACTER_RECORD_NOT_FOUND);
    return CharacterReportResponse.builder()
        .type("TODO")
        .description("TODO")
        .startDate(characterRecord.getStartDate())
        .endDate(characterRecord.getEndDate())
        .valueReports(characterRecord.getValueReports())
        .build();
  }
}
