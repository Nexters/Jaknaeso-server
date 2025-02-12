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
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.ValueReports;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterReportResponse;
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
  @Autowired private CharacterRecordRepository characterRecordRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @AfterEach
  void tearDown() {
    characterRecordRepository.deleteAllInBatch();
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  private CharacterReportCommand createCharacterReportCommand(Long memberId, Long bundleId) {
    return new CharacterReportCommand(memberId, bundleId);
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
        characterRecordRepository.save(
            CharacterRecord.builder()
                .characterNo("첫번째")
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
    class whenCharacterRecordNotFound {

      @Test
      @DisplayName("CHARACTER_RECORD_NOT_FOUND 예외를 던진다.")
      void shouldThrowException() {
        thenThrownBy(() -> sut.getCharacterReport(createCharacterReportCommand(1L, 1L)))
            .hasMessage(CustomException.CHARACTER_RECORD_NOT_FOUND.getMessage());
      }
    }

    @Nested
    @DisplayName("캐릭터 기록을 찾으면")
    class whenCharacterRecordFound {

      @Test
      @DisplayName("회원의 캐릭터 분석 정보를 반환한다.")
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

        final CharacterRecord characterRecord =
            characterRecordRepository.save(
                CharacterRecord.builder()
                    .characterNo("첫번째")
                    .startDate(LocalDate.now().minusDays(15))
                    .endDate(LocalDate.now())
                    .member(member)
                    .surveyBundle(bundle)
                    .valueReports(ValueReports.of(weights, List.of(submission)).getReports())
                    .build());

        final CharacterReportResponse actual =
            sut.getCharacterReport(createCharacterReportCommand(member.getId(), bundle.getId()));

        // TODO: 캐릭터 정보 저장 후 테스트 보완
        then(actual)
            .extracting("startDate", "endDate")
            .containsExactly(LocalDate.now().minusDays(15), LocalDate.now());
      }
    }
  }
}
