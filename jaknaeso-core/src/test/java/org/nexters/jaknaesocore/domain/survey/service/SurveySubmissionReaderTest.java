package org.nexters.jaknaesocore.domain.survey.service;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.nexters.jaknaesocore.domain.survey.repository.BundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class SurveySubmissionReaderTest extends IntegrationTest {

  @Autowired private SurveySubmissionReader surveySubmissionReader;

  @Autowired private MemberRepository memberRepository;
  @Autowired private BundleRepository bundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @DisplayName("회원이 제출한 설문 ID를 가져온다.")
  @Test
  void getSubmittedSurveyIds() {
    Member member = Member.create();
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    bundleRepository.save(surveyBundle);
    BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
    MultipleChoiceSurvey multipleChoiceSurvey = new MultipleChoiceSurvey("질문내용", surveyBundle);
    surveyRepository.saveAll(List.of(balanceSurvey, multipleChoiceSurvey));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(1.0).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(2.0).build());
    SurveyOption balanceOption =
        SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
    SurveyOption multipleChoiceOption1 =
        SurveyOption.builder().survey(multipleChoiceSurvey).scores(scores).content("1점").build();
    SurveyOption multipleChoiceOption2 =
        SurveyOption.builder().survey(multipleChoiceSurvey).scores(scores).content("2점").build();
    surveyOptionRepository.saveAll(
        List.of(balanceOption, multipleChoiceOption1, multipleChoiceOption2));
    SurveySubmission surveySubmission =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(balanceOption)
            .survey(balanceSurvey)
            .build();
    surveySubmissionRepository.save(surveySubmission);

    Set<Long> submittedSurveyIds = surveySubmissionReader.getSubmittedSurveyIds(member.getId());
    then(submittedSurveyIds).hasSize(1).containsExactly(balanceSurvey.getId());
  }

  @DisplayName("회원이 제출한 설문이 없으면 빈 Set을 가져온다.")
  @Test
  void getSubmittedSurveyIds_Empty() {
    Member member = Member.create();
    memberRepository.save(member);

    Set<Long> submittedSurveyIds = surveySubmissionReader.getSubmittedSurveyIds(member.getId());

    then(submittedSurveyIds).isEmpty();
  }
}
