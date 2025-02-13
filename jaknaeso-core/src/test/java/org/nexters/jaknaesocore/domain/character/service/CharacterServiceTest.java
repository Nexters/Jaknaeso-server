package org.nexters.jaknaesocore.domain.character.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.Character;
import org.nexters.jaknaesocore.domain.character.model.CharacterType;
import org.nexters.jaknaesocore.domain.character.model.CharacterValueReport;
import org.nexters.jaknaesocore.domain.character.model.ValueReports;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRepository;
import org.nexters.jaknaesocore.domain.character.repository.CharacterValueReportRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.Survey;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;

class CharacterServiceTest extends IntegrationTest {

  @Autowired private CharacterService sut;

  @Autowired private MemberRepository memberRepository;
  @Autowired private CharacterRepository characterRepository;
  @Autowired private CharacterValueReportRepository characterValueReportRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @AfterEach
  void tearDown() {
    characterValueReportRepository.deleteAllInBatch();
    characterRepository.deleteAllInBatch();
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  private CharacterCommand createCharacterCommand(Long memberId, Long bundleId) {
    return new CharacterCommand(memberId, bundleId);
  }

  private CharacterValueReportCommand createCharacterReportCommand(Long memberId, Long bundleId) {
    return new CharacterValueReportCommand(memberId, bundleId);
  }

  @Nested
  @DisplayName("getCharacter 메소드는")
  class getCharacter {

    @Nested
    @DisplayName("캐릭터 기록을 찾지 못하면")
    class whenCharacterNotFound {

      @Test
      @DisplayName("CHARACTER_RECORD_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacter(createCharacterCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_RECORD_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾았고")
    class whenCharacterFound {

      @Nested
      @DisplayName("bundleId가 null이라면")
      class whenBundleIdIsNull {

        @Test
        @DisplayName("회원의 현재 캐릭터 정보를 반환한다.")
        void shouldReturnCurrentCharacter() {
          final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
          final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
          characterRepository.save(
              Character.builder()
                  .characterNo("첫번째")
                  .characterType(CharacterType.SUCCESS)
                  .startDate(LocalDate.now().minusDays(15))
                  .endDate(LocalDate.now())
                  .member(member)
                  .surveyBundle(bundle)
                  .build());

          final CharacterResponse actual =
              sut.getCharacter(createCharacterCommand(member.getId(), null));

          then(actual)
              .extracting("characterNo", "characterType")
              .containsExactly("첫번째", CharacterType.SUCCESS.getName());
        }
      }

      @Nested
      @DisplayName("bundleId가 null이 아니라면")
      class whenBundleIdIsNotNull {

        @Test
        @DisplayName("회원의 특정 캐릭터 정보를 반환한다.")
        void shouldReturnSpecificCharacter() {
          final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
          final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
          characterRepository.save(
              Character.builder()
                  .characterNo("첫번째")
                  .characterType(CharacterType.SUCCESS)
                  .startDate(LocalDate.now().minusDays(15))
                  .endDate(LocalDate.now())
                  .member(member)
                  .surveyBundle(bundle)
                  .build());

          final CharacterResponse actual =
              sut.getCharacter(createCharacterCommand(member.getId(), bundle.getId()));

          then(actual)
              .extracting("characterNo", "characterType")
              .containsExactly("첫번째", CharacterType.SUCCESS.getName());
        }
      }
    }
  }

  @Nested
  @DisplayName("getCharacters 메소드는")
  class getCharacters {

    @Nested
    @DisplayName("유저를 찾지 못하면")
    class whenMemberNotFound {

      @Test
      @DisplayName("MEMBER_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacters(1L))
            .hasMessage(CustomException.MEMBER_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("유저를 찾으면")
    class whenMemberFound {

      @Test
      @DisplayName("회원의 캐릭터 리스트를 반환한다.")
      void shouldReturnCharacters() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        characterRepository.save(
            Character.builder()
                .characterNo("첫번째")
                .characterType(CharacterType.SUCCESS)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(bundle)
                .build());

        final CharactersResponse actual = sut.getCharacters(member.getId());

        assertAll(
            () -> then(actual.characters()).hasSize(1),
            () ->
                then(actual.characters().get(0))
                    .extracting("characterNo", "bundleId")
                    .containsExactly("첫번째", bundle.getId()));
      }
    }
  }

  @Nested
  @DisplayName("getCharacterReport 메소드는")
  class getCharacterReport {

    @Nested
    @DisplayName("캐릭터 기록을 찾지 못하면")
    class whenCharacterNotFound {

      @Test
      @DisplayName("CHARACTER_RECORD_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacterReport(createCharacterReportCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_RECORD_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾으면")
    class whenCharacterFound {

      @Test
      @DisplayName("회원의 캐릭터 가치관 분석 정보를 반환한다.")
      void shouldReturnReport() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        final Survey survey =
            surveyRepository.save(
                new BalanceSurvey(
                    "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                    bundle));
        final List<KeywordScore> scores =
            List.of(KeywordScore.builder().keyword(SELF_DIRECTION).score(BigDecimal.ONE).build());
        final SurveyOption option =
            surveyOptionRepository.save(
                SurveyOption.builder()
                    .survey(survey)
                    .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                    .scores(scores)
                    .build());
        final SurveySubmission submission =
            surveySubmissionRepository.save(
                SurveySubmission.builder()
                    .member(member)
                    .survey(survey)
                    .selectedOption(option)
                    .build());

        final Map<Keyword, BigDecimal> weights = new HashMap<>();
        weights.put(SELF_DIRECTION, BigDecimal.valueOf(100));

        final Character character =
            characterRepository.save(
                Character.builder()
                    .characterNo("첫번째")
                    .characterType(CharacterType.SELF_DIRECTION)
                    .startDate(LocalDate.now().minusDays(15))
                    .endDate(LocalDate.now())
                    .member(member)
                    .surveyBundle(bundle)
                    .build());
        characterValueReportRepository.save(
            new CharacterValueReport(
                character, ValueReports.of(weights, List.of(submission)).getReports()));

        final CharacterValueReportResponse actual =
            sut.getCharacterReport(createCharacterReportCommand(member.getId(), bundle.getId()));

        then(actual.valueReports())
            .usingRecursiveComparison()
            .isEqualTo(ValueReports.of(weights, List.of(submission)).getReports());
      }
    }
  }
}
