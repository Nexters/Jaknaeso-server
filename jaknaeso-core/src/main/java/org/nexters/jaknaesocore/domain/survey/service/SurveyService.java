package org.nexters.jaknaesocore.domain.survey.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryDetailResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionCommand;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SurveyService {

  private final MemberRepository memberRepository;
  private final SurveyBundleRepository surveyBundleRepository;
  private final SurveySubmissionRepository surveySubmissionRepository;
  private final SurveyRepository surveyRepository;

  @Transactional(readOnly = true)
  public SurveyResponse getNextSurvey(final Long bundleId, final Long memberId) {
    SurveyBundle bundle =
        surveyBundleRepository
            .findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("설문 번들을 찾을 수 없습니다."));
    List<Survey> submittedSurvey = getSubmittedSurvey(memberId);
    Survey unSubmittedSurvey = bundle.getUnSubmittedSurvey(submittedSurvey);

    return SurveyResponse.of(unSubmittedSurvey);
  }

  private List<Survey> getSubmittedSurvey(final Long memberId) {
    List<SurveySubmission> surveySubmissionsByMember =
        surveySubmissionRepository.findByMember_IdAndDeletedAtIsNull(memberId);
    return new SurveySubscriptions(surveySubmissionsByMember).getSubmittedSurvey(memberId);
  }

  @Transactional(readOnly = true)
  public SurveyHistoryResponse getSurveyHistory(final Long memberId) {
    SurveySubmission latestSubmission = findLatestSubmission(memberId);
    if (latestSubmission == null) {
      return createInitialSurveyHistory();
    }
    SurveyBundle bundle = latestSubmission.getSurvey().getSurveyBundle();
    Long currentBundleId = bundle.getId();
    List<SurveySubmission> submissions = findSubmissionsForBundle(memberId, currentBundleId);

    if (bundle.isAllSubmitted(submissions)) {
      Long nextBundleId =
          surveyBundleRepository
              .findFirstByIdGreaterThanOrderByIdAsc(currentBundleId)
              .map(SurveyBundle::getId)
              .orElseThrow(() -> CustomException.NOT_READY_FOR_NEXT_BUNDLE);
      return createNextBundleSurveyHistory(nextBundleId);
    }
    return createCurrentBundleSurveyHistory(currentBundleId, submissions);
  }

  private SurveySubmission findLatestSubmission(final Long memberId) {
    return surveySubmissionRepository
        .findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId)
        .orElse(null);
  }

  private List<SurveySubmission> findSubmissionsForBundle(
      final Long memberId, final Long bundleId) {
    return surveySubmissionRepository.findByMember_IdAndSurvey_SurveyBundle_Id(memberId, bundleId);
  }

  private SurveyHistoryResponse createInitialSurveyHistory() {
    return new SurveyHistoryResponse(1L, List.of(), 1);
  }

  private SurveyHistoryResponse createNextBundleSurveyHistory(final Long bundleId) {
    return new SurveyHistoryResponse(bundleId, List.of(), 1);
  }

  private SurveyHistoryResponse createCurrentBundleSurveyHistory(
      Long bundleId, List<SurveySubmission> submissions) {
    List<SurveyHistoryDetailResponse> historyDetails =
        submissions.stream()
            .map(submission -> new SurveyHistoryDetailResponse(submission.getId()))
            .toList();

    return new SurveyHistoryResponse(bundleId, historyDetails, submissions.size() + 1);
  }

  @Transactional
  public void submitSurvey(
      Long surveyId, Long memberId, SurveySubmissionCommand request, LocalDateTime submittedAt) {
    Survey survey =
        surveyRepository.findById(surveyId).orElseThrow(() -> CustomException.SURVEY_NOT_FOUND);
    SurveyOption surveyOption = survey.getOptionById(request.optionId());
    Member member =
        memberRepository
            .findByIdAndDeletedAtIsNull(memberId)
            .orElseThrow(() -> CustomException.MEMBER_NOT_FOUND);

    SurveySubmission surveySubmission =
        SurveySubmission.create(member, survey, surveyOption, request.comment(), submittedAt);

    surveySubmissionRepository.save(surveySubmission);
  }
}
