package org.nexters.jaknaesocore.domain.question.service;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.question.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.question.model.*;
import org.nexters.jaknaesocore.domain.question.repository.BundleRepository;
import org.nexters.jaknaesocore.domain.question.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.question.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class SurveyServiceTest extends IntegrationTest {

  @Autowired private SurveyService surveyService;

  @Autowired private MemberRepository memberRepository;
  @Autowired private BundleRepository bundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;

  @DisplayName("설문을 조회한다.")
  @Test
  void getSurvey() {
    // given
    Member member = Member.create();
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    bundleRepository.save(surveyBundle);
    BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
    surveyRepository.save(balanceSurvey);
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(1.0).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(2.0).build());
    SurveyOption option =
        SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
    surveyOptionRepository.save(option);

    // when
    SurveyResponse survey = surveyService.getSurvey(surveyBundle.getId(), member.getId());
    // then
    then(survey)
        .extracting("id", "contents", "surveyType")
        .containsExactly(balanceSurvey.getId(), "질문내용", "BALANCE");
    then(survey.options())
        .extracting("id", "optionContents")
        .containsExactly(tuple(option.getId(), "질문 옵션 내용"));
  }
}
