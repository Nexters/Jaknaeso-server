package org.nexters.jaknaesocore.domain.survey.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.springframework.beans.factory.annotation.Autowired;

class SurveySubmissionRepositoryTest extends IntegrationTest {

  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;

  @AfterEach
  void tearDown() {
    surveySubmissionRepository.deleteAll();
    surveyOptionRepository.deleteAll();
    surveyRepository.deleteAll();
    surveyBundleRepository.deleteAll();
    memberRepository.deleteAll();
  }

  @DisplayName("회원이 제출한 설문 ID를 가져온다.")
  @Test
  void findByMember_IdAndDeletedAtIsNull() {
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
}
