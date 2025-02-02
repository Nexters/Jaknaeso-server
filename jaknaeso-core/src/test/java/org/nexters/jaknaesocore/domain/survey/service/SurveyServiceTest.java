package org.nexters.jaknaesocore.domain.survey.service;

import static org.assertj.core.api.BDDAssertions.*;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class SurveyServiceTest extends IntegrationTest {

  @Autowired private SurveyService surveyService;

  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;

  @DisplayName("설문을 조회한다.")
  @Test
  void getNextSurvey() {
    // given
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
}
