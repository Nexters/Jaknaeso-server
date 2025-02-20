package org.nexters.jaknaesocore.domain.character.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.ADVENTURE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.BENEVOLENCE;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SUCCESS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueCharacterRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueReportRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.model.Surveys;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class CharacterServiceTest extends IntegrationTest {

  @Autowired private CharacterService sut;

  @Autowired private MemberRepository memberRepository;
  @Autowired private ValueReportRepository valueReportRepository;
  @Autowired private ValueCharacterRepository valueCharacterRepository;
  @Autowired private CharacterRecordRepository characterRecordRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @AfterEach
  void tearDown() {
    valueReportRepository.deleteAllInBatch();
    characterRecordRepository.deleteAllInBatch();
    valueCharacterRepository.deleteAllInBatch();
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  private CharacterCommand createCharacterCommand(Long memberId, Long characterId) {
    return new CharacterCommand(memberId, characterId);
  }

  private CharacterValueReportCommand createCharacterReportCommand(
      Long memberId, Long characterId) {
    return new CharacterValueReportCommand(memberId, characterId);
  }

  @Nested
  @DisplayName("getCharacter 메소드는")
  class getCharacter {

    @Nested
    @DisplayName("캐릭터 기록을 찾지 못하면")
    class whenCharacterRecordNotFound {

      @Test
      @DisplayName("CHARACTER_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacter(createCharacterCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾으면")
    class whenCharacterRecordFound {

      @Test
      @DisplayName("회원의 특정 캐릭터 정보를 반환한다.")
      void shouldReturnCharacter() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        final ValueCharacter valueCharacter =
            valueCharacterRepository.save(new ValueCharacter("성취를 쫓는 노력가", "성공 캐릭터 설명", SUCCESS));
        final CharacterRecord characterRecord =
            characterRecordRepository.save(
                CharacterRecord.builder()
                    .characterNo("첫번째")
                    .valueCharacter(valueCharacter)
                    .startDate(LocalDate.now().minusDays(15))
                    .endDate(LocalDate.now())
                    .member(member)
                    .surveyBundle(bundle)
                    .build());

        final CharacterResponse actual =
            sut.getCharacter(createCharacterCommand(member.getId(), characterRecord.getId()));

        then(actual)
            .extracting("characterNo", "characterType")
            .containsExactly("첫번째", valueCharacter.getKeyword());
      }
    }
  }

  @Nested
  @DisplayName("getCurrentCharacter 메소드는")
  class getCurrentCharacter {

    @Nested
    @DisplayName("캐릭터 기록을 찾지 못하면")
    class whenCharacterRecordNotFound {

      @Test
      @DisplayName("CHARACTER_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacter(createCharacterCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾으면")
    class whenCharacterRecordFound {

      @Test
      @DisplayName("회원의 현재 캐릭터 정보를 반환한다.")
      void shouldReturnCharacter() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        final ValueCharacter valueCharacter =
            valueCharacterRepository.save(new ValueCharacter("성취를 쫓는 노력가", "성공 캐릭터 설명", SUCCESS));
        characterRecordRepository.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .valueCharacter(valueCharacter)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(bundle)
                .build());

        final CharacterResponse actual = sut.getCurrentCharacter(member.getId());

        then(actual)
            .extracting("characterNo", "characterType")
            .containsExactly("첫번째", valueCharacter.getKeyword());
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
        final SurveyBundle completeBundle = surveyBundleRepository.save(new SurveyBundle());
        final ValueCharacter valueCharacter =
            valueCharacterRepository.save(new ValueCharacter("성취를 쫓는 노력가", "성공 캐릭터 설명", SUCCESS));
        characterRecordRepository.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
                .valueCharacter(valueCharacter)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(completeBundle)
                .build());

        final SurveyBundle incompleteBundle = surveyBundleRepository.save(new SurveyBundle());
        characterRecordRepository.save(
            CharacterRecord.builder()
                .characterNo("두번째")
                .valueCharacter(null)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now())
                .member(member)
                .surveyBundle(incompleteBundle)
                .build());

        final BalanceSurvey survey =
            surveyRepository.save(
                new BalanceSurvey(
                    "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                    incompleteBundle));
        final SurveyOption option =
            surveyOptionRepository.save(
                SurveyOption.builder()
                    .survey(survey)
                    .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                    .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.ONE)))
                    .build());
        surveySubmissionRepository.save(
            SurveySubmission.builder()
                .survey(survey)
                .member(member)
                .selectedOption(option)
                .build());

        final CharactersResponse actual = sut.getCharacters(member.getId());

        assertAll(
            () -> then(actual.characters()).hasSize(2),
            () ->
                then(actual.characters().get(0))
                    .extracting("characterNo", "bundleId")
                    .containsExactly("첫번째", completeBundle.getId()),
            () ->
                then(actual.characters().get(1))
                    .extracting("characterNo", "bundleId")
                    .containsExactly("두번째", incompleteBundle.getId())); // 추후 같이 수정
      }
    }
  }

  @Nested
  @DisplayName("getCharacterRecordReport 메소드는")
  class getCharacterRecordReport {

    @Nested
    @DisplayName("캐릭터 기록을 찾지 못하면")
    class whenCharacterRecordNotFound {

      @Test
      @DisplayName("CHARACTER_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacterReport(createCharacterReportCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾으면")
    class whenCharacterRecordFound {

      @Transactional
      @Test
      @DisplayName("회원의 캐릭터 가치관 분석 정보를 반환한다.")
      void shouldReturnReport() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        final ValueReport valueReport =
            valueReportRepository.save(
                ValueReport.of(SELF_DIRECTION, ScaledBigDecimal.of(BigDecimal.valueOf(100))));
        final CharacterRecord characterRecord =
            characterRecordRepository.save(
                CharacterRecord.builder()
                    .characterNo("첫번째")
                    .valueReports(List.of(valueReport))
                    .startDate(LocalDate.now().minusDays(15))
                    .endDate(LocalDate.now())
                    .member(member)
                    .surveyBundle(bundle)
                    .build());

        final CharacterValueReportResponse actual =
            sut.getCharacterReport(
                createCharacterReportCommand(member.getId(), characterRecord.getId()));

        then(actual.valueReports()).usingRecursiveComparison().isEqualTo(List.of(valueReport));
      }
    }
  }

  @Nested
  @DisplayName("createFirstCharacter 메소드는")
  class createFirstCharacter {

    @Transactional
    @Test
    @DisplayName("온보딩 후 회원의 첫번째 캐릭터를 생성한다.")
    void shouldCreateCharacter() {
      final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
      final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());

      final BalanceSurvey survey1 =
          surveyRepository.save(
              new BalanceSurvey(
                  "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                  bundle));
      final BalanceSurvey survey2 =
          surveyRepository.save(new BalanceSurvey("독립에 대한 고민이 깊어지는 요즘... 드디어 결정을 내렸다.", bundle));
      final BalanceSurvey survey3 =
          surveyRepository.save(
              new BalanceSurvey("바쁜 일상에 지쳐버린 나. 여가 시간을 더 의미 있게 보내고 싶어졌다.", bundle));
      final BalanceSurvey survey4 =
          surveyRepository.save(new BalanceSurvey("회사에서 새로운 평가 시스템을 도입한다. 당신의 선택은?", bundle));
      final BalanceSurvey survey5 =
          surveyRepository.save(
              new BalanceSurvey("연애를 시작한지도 어연 3개월, 그 사람과 나의 연애는 꽤 잘 맞는다. 우리의 관계는...", bundle));

      final SurveyOption option1 =
          surveyOptionRepository.save(
              SurveyOption.builder()
                  .survey(survey1)
                  .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                  .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.ONE)))
                  .build());
      final SurveyOption option2 =
          surveyOptionRepository.save(
              SurveyOption.builder()
                  .survey(survey2)
                  .content("내 취향대로 꾸민 집에서 자유롭게 생활하기")
                  .scores(List.of(KeywordScore.of(SELF_DIRECTION, BigDecimal.ONE)))
                  .build());
      final SurveyOption option3 =
          surveyOptionRepository.save(
              SurveyOption.builder()
                  .survey(survey3)
                  .content("매년 새로운 취미에 도전하며 색다른 즐거움 찾기")
                  .scores(List.of(KeywordScore.of(ADVENTURE, BigDecimal.ONE)))
                  .build());
      final SurveyOption option4 =
          surveyOptionRepository.save(
              SurveyOption.builder()
                  .survey(survey4)
                  .content("업무 성과에 따라 차등 보너스를 지급한다")
                  .scores(List.of(KeywordScore.of(SUCCESS, BigDecimal.ONE)))
                  .build());
      final SurveyOption option5 =
          surveyOptionRepository.save(
              SurveyOption.builder()
                  .survey(survey5)
                  .content("서로의 일상 속에서 따뜻하게 지지하는 관계")
                  .scores(List.of(KeywordScore.of(BENEVOLENCE, BigDecimal.ONE)))
                  .build());

      final List<SurveySubmission> submissions =
          surveySubmissionRepository.saveAll(
              List.of(
                  SurveySubmission.builder()
                      .survey(survey1)
                      .selectedOption(option1)
                      .submittedAt(LocalDateTime.of(2025, 2, 11, 0, 0))
                      .build(),
                  SurveySubmission.builder()
                      .survey(survey2)
                      .selectedOption(option2)
                      .submittedAt(LocalDateTime.of(2025, 2, 12, 0, 0))
                      .build(),
                  SurveySubmission.builder()
                      .survey(survey3)
                      .selectedOption(option3)
                      .submittedAt(LocalDateTime.of(2025, 2, 13, 0, 0))
                      .build(),
                  SurveySubmission.builder()
                      .survey(survey4)
                      .selectedOption(option4)
                      .submittedAt(LocalDateTime.of(2025, 2, 14, 0, 0))
                      .build(),
                  SurveySubmission.builder()
                      .survey(survey5)
                      .selectedOption(option5)
                      .submittedAt(LocalDateTime.of(2025, 2, 15, 0, 0))
                      .build()));

      sut.createFirstCharacter(
          member,
          bundle,
          Surveys.of(List.of(survey1, survey2, survey3, survey4, survey5)),
          submissions);

      assertAll(
          () -> then(characterRecordRepository.findAll()).hasSize(1),
          () -> then(valueReportRepository.findAll()).hasSize(4));
    }
  }
}
