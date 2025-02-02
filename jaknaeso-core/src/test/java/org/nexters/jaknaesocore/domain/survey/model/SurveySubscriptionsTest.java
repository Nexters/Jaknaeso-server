package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.test.util.ReflectionTestUtils;

class SurveySubscriptionsTest {

  @DisplayName("회원이 제출한 설문들을 가져온다")
  @Test
  void getSubmittedSurvey() {
    // given
    Member member = Member.create();
    ReflectionTestUtils.setField(member, "id", 1L);

    SurveyBundle surveyBundle = new SurveyBundle();
    BalanceSurvey survey1 = new BalanceSurvey("설문1", surveyBundle);
    MultipleChoiceSurvey survey2 = new MultipleChoiceSurvey("설문2", surveyBundle);

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("옵션1").build();

    SurveySubmission submission1 =
        SurveySubmission.builder().member(member).selectedOption(option1).survey(survey1).build();

    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("옵션2").build();

    SurveySubmission submission2 =
        SurveySubmission.builder().member(member).selectedOption(option2).survey(survey2).build();

    // when
    SurveySubscriptions subscriptions = new SurveySubscriptions(List.of(submission1, submission2));
    List<Survey> submittedSurveys = subscriptions.getSubmittedSurvey(1L);

    // then
    then(submittedSurveys).hasSize(2).containsExactlyInAnyOrder(survey1, survey2);
  }

  @DisplayName("다른 회원이 제출한 설문은 포함되지 않는다")
  @Test
  void getSubmittedSurvey_OtherMember() {
    // given
    Member member1 = Member.create();
    ReflectionTestUtils.setField(member1, "id", 1L);

    Member member2 = Member.create();
    ReflectionTestUtils.setField(member2, "id", 2L);

    SurveyBundle surveyBundle = new SurveyBundle();
    BalanceSurvey survey1 = new BalanceSurvey("설문1", surveyBundle);
    MultipleChoiceSurvey survey2 = new MultipleChoiceSurvey("설문2", surveyBundle);

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("옵션1").build();

    SurveySubmission submission1 =
        SurveySubmission.builder().member(member1).selectedOption(option1).survey(survey1).build();

    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("옵션2").build();

    SurveySubmission submission2 =
        SurveySubmission.builder().member(member2).selectedOption(option2).survey(survey2).build();

    // when
    SurveySubscriptions subscriptions = new SurveySubscriptions(List.of(submission1, submission2));
    List<Survey> submittedSurveys = subscriptions.getSubmittedSurvey(1L);

    // then
    then(submittedSurveys).hasSize(1).containsExactly(survey1);
  }

  @DisplayName("회원이 제출한 설문이 없으면 빈 리스트를 반환한다")
  @Test
  void getSubmittedSurvey_Empty() {
    // given
    Member member = Member.create();
    ReflectionTestUtils.setField(member, "id", 1L);

    // when
    SurveySubscriptions subscriptions = new SurveySubscriptions(List.of());
    List<Survey> submittedSurveys = subscriptions.getSubmittedSurvey(1L);

    // then
    then(submittedSurveys).isEmpty();
  }
}
