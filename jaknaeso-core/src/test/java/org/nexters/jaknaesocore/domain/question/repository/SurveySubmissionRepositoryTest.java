package org.nexters.jaknaesocore.domain.question.repository;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.question.model.*;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class SurveySubmissionRepositoryTest extends IntegrationTest {

  @Autowired private SurveySubmissionRepository surveySubmissionRepository;

  @Autowired private MemberRepository memberRepository;
  @Autowired private BundleRepository bundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;

  @DisplayName("회원이 제출한 설문 ID를 가져온다.")
  @Test
  void findByMember_IdAndDeletedAtIsNull() {
    Member member = Member.create();
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    bundleRepository.save(surveyBundle);
    BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
    surveyRepository.save(balanceSurvey);
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ACHIEVEMENT).score(1).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(2).build());
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
