package org.nexters.jaknaesocore.domain.character.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecords;
import org.nexters.jaknaesocore.domain.character.model.Characters;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueCharacterRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.SimpleCharacterResponses;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.nexters.jaknaesocore.domain.survey.service.event.CreateCharacterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CharacterService {

  private final MemberRepository memberRepository;
  private final CharacterRecordRepository characterRecordRepository;
  private final ValueCharacterRepository valueCharacterRepository;
  private final SurveySubmissionRepository surveySubmissionRepository;

  private Map<Keyword, ValueCharacter> valueCharacters;

  @PostConstruct
  void setUp() {
    valueCharacters =
        valueCharacterRepository.findAll().stream()
            .collect(
                Collectors.toMap(ValueCharacter::getKeyword, valueCharacter -> valueCharacter));
  }

  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(final CharacterCommand command) {
    return characterRecordRepository
        .findByIdAndMemberIdAndDeletedAtIsNullWithMember(command.memberId(), command.characterId())
        .map(
            it ->
                CharacterResponse.builder()
                    .characterId(it.getId())
                    .characterNo(it.getCharacterNo())
                    .valueCharacter(it.getValueCharacter())
                    .name(it.getValueCharacter().getName())
                    .description(it.getValueCharacter().getDescription())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharacterResponse getCurrentCharacter(final Long memberId) {
    return characterRecordRepository
        .findTopByMemberIdAndDeletedAtIsNullWithMember(memberId)
        .map(
            it ->
                CharacterResponse.builder()
                    .characterId(it.getId())
                    .characterNo(it.getCharacterNo())
                    .valueCharacter(it.getValueCharacter())
                    .name(it.getValueCharacter().getName())
                    .description(it.getValueCharacter().getDescription())
                    .startDate(it.getStartDate())
                    .endDate(it.getEndDate())
                    .build())
        .orElseThrow(() -> CustomException.CHARACTER_NOT_FOUND);
  }

  @Transactional(readOnly = true)
  public CharactersResponse getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    final List<CharacterRecord> records =
        characterRecordRepository.findByMemberIdAndDeletedAtIsNullWithMemberAndSurveyBundle(
            memberId);
    final CharacterRecords characterRecords = new CharacterRecords(records);
    final Long bundleId = getLatestBundleId(memberId);

    final SimpleCharacterResponses responses = new SimpleCharacterResponses();
    records.forEach(responses::addCompleteResponse);
    if (characterRecords.isIncompleteCharacter(bundleId)) {
      responses.addIncompleteResponse(bundleId);
    }
    return new CharactersResponse(responses.getResponses());
  }

  private Long getLatestBundleId(final Long memberId) {
    return surveySubmissionRepository
        .findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId)
        .map(it -> it.getSurvey().getSurveyBundle().getId())
        .orElse(null);
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

  @EventListener
  @Transactional(propagation = Propagation.MANDATORY)
  public void createCharacter(final CreateCharacterEvent event) {
    final Characters characters =
        Characters.builder()
            .member(event.member())
            .bundle(event.bundle())
            .scores(event.scores())
            .submissions(event.submissions())
            .valueCharacters(valueCharacters)
            .build();
    characterRecordRepository.save(characters.provideCharacterRecord());

    log.info(
        "Handled CreateCharacterEvent for memberId {} and bundleId {}",
        event.member().getId(),
        event.bundle().getId());
  }
}
