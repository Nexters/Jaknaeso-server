package org.nexters.jaknaesocore.domain.character.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
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
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @AfterEach
  void tearDown() {
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
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
      void shouldReturnQuestions() {
        final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());
        final Survey survey =
            surveyRepository.save(
                new BalanceSurvey(
                    "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                    bundle));
        final List<KeywordScore> scores =
            List.of(
                KeywordScore.builder()
                    .keyword(Keyword.SELF_DIRECTION)
                    .score(BigDecimal.ONE)
                    .build(),
                KeywordScore.builder().keyword(Keyword.STABILITY).score(BigDecimal.TWO).build());
        final SurveyOption option =
            surveyOptionRepository.save(
                SurveyOption.builder()
                    .survey(survey)
                    .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                    .scores(scores)
                    .build());
        surveySubmissionRepository.save(
            SurveySubmission.builder()
                .member(member)
                .survey(survey)
                .selectedOption(option)
                .build());

        List<CharacterResponse> actual = sut.getCharacters(member.getId());

        assertAll(
            () -> then(actual).hasSize(1),
            () ->
                then(actual.get(0))
                    .extracting("ordinalNumber", "bundleId")
                    .containsExactly(1L, bundle.getId()));
      }
    }
  }
}
