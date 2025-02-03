package org.nexters.jaknaesocore.domain.survey.repository;

import static org.assertj.core.api.BDDAssertions.*;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.springframework.beans.factory.annotation.Autowired;

class SurveySubmissionRepositoryTest extends IntegrationTest {

  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @Autowired private MemberRepository memberRepository;
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

  @DisplayName("회원이 제출한 설문 ID를 가져온다.")
  @Test
  void findByMember_IdAndDeletedAtIsNull() {
    Member member = Member.create();
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
    SurveySubmission surveySubmission =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option)
            .survey(balanceSurvey)
            .build();
    surveySubmissionRepository.save(surveySubmission);

    var submittedSurveyIds =
        surveySubmissionRepository.findByMember_IdAndDeletedAtIsNull(member.getId());

    then(submittedSurveyIds)
        .hasSize(1)
        .extracting("id", "deletedAt")
        .containsExactly(tuple(surveySubmission.getId(), null));
  }

  @Transactional
  @Test
  void 회원이_제출한_가장_마지막_설문을_조회한다() {
    // given
    Member member = Member.create();
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
    Optional<SurveySubmission> latestSubmission =
        surveySubmissionRepository.findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
            member.getId());
    // then
    then(latestSubmission).isPresent();
    then(latestSubmission.get())
        .extracting("id", "survey.surveyBundle.id")
        .containsExactly(submission2.getId(), surveyBundle.getId());
  }

  @Test
  void 번들에서_제출한_설문을_조회한다() {
    // given
    Member member = Member.create();
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

    // then
    then(surveySubmissionRepository.findByMember_IdAndSurvey_SurveyBundle_Id(
            member.getId(), surveyBundle.getId()))
        .hasSize(2)
        .extracting("id")
        .containsExactly(submission1.getId(), submission2.getId());
  }
}
