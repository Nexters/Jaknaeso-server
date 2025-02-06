package org.nexters.jaknaesocore.domain.survey.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
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
    return findLatestSubmission(memberId)
        .map(this::classifySubmission)
        .orElseGet(this::createInitialSurveyHistory);
  }

  private Optional<SurveySubmission> findLatestSubmission(final Long memberId) {
    return surveySubmissionRepository.findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
        memberId);
  }

  private SurveyHistoryResponse classifySubmission(SurveySubmission latestSubmission) {
    SurveyBundle bundle = latestSubmission.getSurvey().getSurveyBundle();
    List<SurveySubmission> submissions =
        findSubmissionsForBundle(latestSubmission.getMember().getId(), bundle.getId());

    boolean isSubmittedToday = latestSubmission.isSubmittedByDate(LocalDate.now());
    if (isSubmittedToday) {
      return createCurrentBundleSurveyHistory(bundle.getId(), submissions, isSubmittedToday);
    }

    if (bundle.isAllSubmitted(submissions)) {
      return getNextBundleHistory(bundle.getId());
    }
    return createCurrentBundleSurveyHistory(bundle.getId(), submissions, isSubmittedToday);
  }

  private List<SurveySubmission> findSubmissionsForBundle(
      final Long memberId, final Long bundleId) {
    return surveySubmissionRepository.findByMember_IdAndSurvey_SurveyBundle_Id(memberId, bundleId);
  }

  private SurveyHistoryResponse createInitialSurveyHistory() {
    return new SurveyHistoryResponse(1L, List.of(), 1, false);
  }

  private SurveyHistoryResponse createNextBundleSurveyHistory(final Long bundleId) {
    return new SurveyHistoryResponse(bundleId, List.of(), 1, false);
  }

  private SurveyHistoryResponse createCurrentBundleSurveyHistory(
      Long bundleId, List<SurveySubmission> submissions, boolean isSubmittedToday) {
    List<SurveyHistoryDetailResponse> historyDetails =
        submissions.stream().map(BaseEntity::getId).map(SurveyHistoryDetailResponse::of).toList();
    if (isSubmittedToday) {
      return new SurveyHistoryResponse(
          bundleId, historyDetails, historyDetails.size(), isSubmittedToday);
    }
    return new SurveyHistoryResponse(
        bundleId, historyDetails, historyDetails.size() + 1, isSubmittedToday);
  }

  private SurveyHistoryResponse getNextBundleHistory(Long currentBundleId) {
    return surveyBundleRepository
        .findFirstByIdGreaterThanOrderByIdAsc(currentBundleId)
        .map(bundle -> createNextBundleSurveyHistory(bundle.getId()))
        .orElseThrow(() -> CustomException.NOT_READY_FOR_NEXT_BUNDLE);
  }

  @Transactional
  public void submitSurvey(SurveySubmissionCommand command, LocalDateTime submittedAt) {
    Survey survey =
        surveyRepository
            .findById(command.surveyId())
            .orElseThrow(() -> CustomException.SURVEY_NOT_FOUND);
    SurveyOption surveyOption = survey.getOptionById(command.optionId());
    Member member =
        memberRepository
            .findByIdAndDeletedAtIsNull(command.memberId())
            .orElseThrow(() -> CustomException.MEMBER_NOT_FOUND);

    SurveySubmission surveySubmission =
        SurveySubmission.create(member, survey, surveyOption, command.comment(), submittedAt);

    surveySubmissionRepository.save(surveySubmission);
  }
}
