package org.nexters.jaknaesocore.domain.character.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.*;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueCharacterRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.SimpleCharacterResponse;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CharacterService {

  private final MemberRepository memberRepository;
  private final CharacterRecordRepository characterRecordRepository;
  private final ValueCharacterRepository valueCharacterRepository;

  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(final CharacterCommand command) {
    return characterRecordRepository
        .findByIdAndMemberIdAndDeletedAtIsNullWithMember(command.characterId(), command.memberId())
        .map(CharacterResponse::of)
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharacterResponse getCurrentCharacter(final Long memberId) {
    return characterRecordRepository
        .findLatestCompletedCharacter(memberId)
        .map(CharacterResponse::of)
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharactersResponse getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    final List<CharacterRecord> records =
        characterRecordRepository.findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(
            memberId);
    return new CharactersResponse(SimpleCharacterResponse.listOf(records));
  }

  @Transactional(readOnly = true)
  public CharacterValueReportResponse getCharacterReport(
      final CharacterValueReportCommand command) {
    final CharacterRecord characterRecord =
        characterRecordRepository
            .findByIdAndDeletedAtIsNullWithValueReports(command.characterId())
            .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
    return CharacterValueReportResponse.of(characterRecord.getValueReports());
  }

  @Transactional
  public void createFirstCharacter(
      Member member,
      SurveyBundle bundle,
      List<KeywordScore> scores,
      List<SurveySubmission> submissions) {
    final List<ValueReport> valueReports = ValueReports.report(scores);
    final ValueCharacter valueCharacter =
        ValueCharacters.of(valueCharacterRepository.findAll()).findTopValueCharacter(valueReports);
    characterRecordRepository.save(
        CharacterRecord.ofFirst(member, bundle, valueCharacter, valueReports, submissions));
  }

  @Transactional
  public void createCharacter(Member member, SurveyBundle bundle, LocalDate startDate) {
    final CharacterRecord previousRecord =
        characterRecordRepository
            .findLatestCompletedCharacter(member.getId())
            .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
    characterRecordRepository.save(CharacterRecord.of(previousRecord, member, bundle, startDate));
  }

  @Transactional
  public void updateCharacter(
      Member member,
      SurveyBundle bundle,
      List<KeywordScore> scores,
      List<SurveySubmission> submissions) {
    final List<ValueReport> valueReports = ValueReports.report(scores);
    final ValueCharacter valueCharacter =
        ValueCharacters.of(valueCharacterRepository.findAll()).findTopValueCharacter(valueReports);
    final CharacterRecord characterRecord =
        characterRecordRepository
            .findByMemberIdAndBundleId(member.getId(), bundle.getId())
            .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
    characterRecord.updateRecord(valueCharacter, valueReports, submissions);
  }
}
