package org.nexters.jaknaesocore.domain.survey.service;

import static org.assertj.core.api.BDDAssertions.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionServiceRequest;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;

class SurveyServiceTest extends IntegrationTest {

  @Autowired private SurveyService surveyService;

  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;

  @AfterEach
  void tearDown() {
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @DisplayName("설문을 조회한다.")
  @Test
  void getNextSurvey() {
    // given
    Member member = Member.create("name", "email");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
    surveyRepository.save(balanceSurvey);
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option =
        SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
    surveyOptionRepository.save(option);

    // when
    SurveyResponse survey = surveyService.getNextSurvey(surveyBundle.getId(), member.getId());
    // then
    then(survey)
        .extracting("id", "contents", "surveyType")
        .containsExactly(balanceSurvey.getId(), "질문내용", "BALANCE");
    then(survey.options())
        .extracting("id", "optionContents")
        .containsExactly(tuple(option.getId(), "질문 옵션 내용"));
  }

  @DisplayName("submitSurvey 메서드는")
  @Nested
  class submitSurvey {

    @DisplayName("회원이 존재하지 않으면")
    @Nested
    class whenMemberNotFound {
      @Test
      @DisplayName("예외를 발생시킨다")
      void throwMemberNotFoundException() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);
        SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
        surveyRepository.save(balanceSurvey);
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option =
            SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
        surveyOptionRepository.save(option);

        SurveySubmissionServiceRequest request =
            new SurveySubmissionServiceRequest(option.getId(), "나는 행복한게 좋으니까");

        // when
        // then
        thenThrownBy(() -> surveyService.submitSurvey(balanceSurvey.getId(), 0L, request))
            .isEqualTo(CustomException.MEMBER_NOT_FOUND);
      }
    }

    @DisplayName("설문을 저장한다")
    @Nested
    class shouldSubmitted {
      @Test
      void 설문에_응답을_제출한다() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);
        SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
        surveyRepository.save(balanceSurvey);
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option =
            SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
        surveyOptionRepository.save(option);

        SurveySubmissionServiceRequest request =
            new SurveySubmissionServiceRequest(option.getId(), "나는 행복한게 좋으니까");

        // when
        surveyService.submitSurvey(balanceSurvey.getId(), member.getId(), request);
        // then
        List<SurveySubmission> submissions = surveySubmissionRepository.findAll();
        then(submissions).hasSize(1);
        then(submissions.getFirst())
            .extracting("member.id", "survey.id", "selectedOption.id", "retrospective")
            .containsExactly(member.getId(), balanceSurvey.getId(), option.getId(), "나는 행복한게 좋으니까");
      }
    }

    @DisplayName("설문이 존재하지 않으면")
    @Nested
    class whenSurveyNotFound {
      @Test
      @DisplayName("예외를 발생시킨다")
      void throwSurveyNotFoundException() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);
        SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
        surveyRepository.save(balanceSurvey);
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option =
            SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
        surveyOptionRepository.save(option);

        SurveySubmissionServiceRequest request =
            new SurveySubmissionServiceRequest(option.getId(), "나는 행복한게 좋으니까");

        // when
        // then
        thenThrownBy(() -> surveyService.submitSurvey(0L, member.getId(), request))
            .isEqualTo(CustomException.SURVEY_NOT_FOUND);
      }
    }
  }

  @Test
  void 설문_기록이_없으면_1번_번들을_제공한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey1).scores(scores).content("안한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey2).scores(scores).content("안한다.").build();
    SurveyOption option5 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option6 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();
    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4, option5, option6));
    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());

    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "surveyHistoryDetails")
        .containsExactly(1L, 1, List.of());
  }

  @Test
  void 설문_기록이_일정_개수_이하이면_기록과_함께_번들ID를_제공한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey1).scores(scores).content("안한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey2).scores(scores).content("안한다.").build();
    SurveyOption option5 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option6 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();
    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4, option5, option6));

    SurveySubmission submission1 =
        SurveySubmission.builder().member(member).selectedOption(option1).survey(survey1).build();
    SurveySubmission submission2 =
        SurveySubmission.builder().member(member).selectedOption(option4).survey(survey2).build();
    surveySubmissionRepository.save(submission1);
    surveySubmissionRepository.save(submission2);
    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());
    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex")
        .containsExactly(surveyBundle.getId(), 3);

    then(response.surveyHistoryDetails())
        .extracting("submissionId")
        .containsExactly(submission1.getId(), submission2.getId());
  }

  @Test
  void 설문_기록이_번들_크기만큼_있으면_다음_번들ID를_제공한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle1 = new SurveyBundle();
    SurveyBundle surveyBundle2 = new SurveyBundle();

    surveyBundleRepository.saveAll(List.of(surveyBundle1, surveyBundle2));

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle1);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle1);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle1);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle1);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));

    SurveySubmission submission1 =
        SurveySubmission.builder().member(member).selectedOption(option1).survey(survey1).build();
    SurveySubmission submission2 =
        SurveySubmission.builder().member(member).selectedOption(option2).survey(survey2).build();
    SurveySubmission submission3 =
        SurveySubmission.builder().member(member).selectedOption(option3).survey(survey3).build();
    SurveySubmission submission4 =
        SurveySubmission.builder().member(member).selectedOption(option4).survey(survey4).build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));

    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());

    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "surveyHistoryDetails")
        .containsExactly(surveyBundle1.getId() + 1L, 1, List.of());
  }

  @Test
  void 진행할_다음_설문_번들이_존재하지_않으면_예외를_발생한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    surveyBundleRepository.save(surveyBundle);

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));

    SurveySubmission submission1 =
        SurveySubmission.builder().member(member).selectedOption(option1).survey(survey1).build();
    SurveySubmission submission2 =
        SurveySubmission.builder().member(member).selectedOption(option2).survey(survey2).build();
    SurveySubmission submission3 =
        SurveySubmission.builder().member(member).selectedOption(option3).survey(survey3).build();
    SurveySubmission submission4 =
        SurveySubmission.builder().member(member).selectedOption(option4).survey(survey4).build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));
    // when
    // then
    thenThrownBy(() -> surveyService.getSurveyHistory(member.getId()))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.NOT_READY_FOR_NEXT_BUNDLE);
  }
}
