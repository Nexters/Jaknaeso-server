package org.nexters.jaknaesocore.domain.survey.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.*;
import org.nexters.jaknaesocore.domain.survey.model.*;
import org.nexters.jaknaesocore.domain.survey.repository.OnboardingSurveyRepository;
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
  private final OnboardingSurveyRepository onboardingSurveyRepository;

  @Transactional(readOnly = true)
  public SurveyResponse getNextSurvey(final Long bundleId, final Long memberId) {
    SurveyBundle bundle =
        surveyBundleRepository
            .findById(bundleId)
            .orElseThrow(() -> CustomException.BUNDLE_NOT_FOUND);
    List<Survey> submittedSurvey = getSubmittedSurvey(memberId);
    Survey nextSurvey = bundle.getNextSurvey(submittedSurvey);

    return SurveyResponse.of(nextSurvey);
  }

  private List<Survey> getSubmittedSurvey(final Long memberId) {
    List<SurveySubmission> surveySubmissionsByMember =
        surveySubmissionRepository.findByMember_IdAndDeletedAtIsNull(memberId);
    return new SurveySubmissions(surveySubmissionsByMember).getSubmittedSurvey(memberId);
  }

  @Transactional(readOnly = true)
  public SurveyHistoryResponse getSurveyHistory(final Long memberId) {
    SurveySubmission latestSubmission = findLatestSubmission(memberId);
    return classifySubmission(latestSubmission);
  }

  private SurveySubmission findLatestSubmission(final Long memberId) {
    return surveySubmissionRepository
        .findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId)
        .orElseThrow(() -> CustomException.NOT_PROCEED_ONBOARDING);
  }

  private SurveyHistoryResponse classifySubmission(SurveySubmission latestSubmission) {
    SurveyBundle bundle = latestSubmission.getSurvey().getSurveyBundle();
    List<SurveySubmission> submissions =
        findSubmissionsForBundle(latestSubmission.getMember().getId(), bundle.getId());

    boolean isCompleted = latestSubmission.isSubmittedByDate(LocalDate.now());
    if (isCompleted) {
      return createCurrentBundleSurveyHistory(bundle.getId(), submissions, isCompleted);
    }

    if (bundle.isAllSubmitted(submissions)) {
      return getNextBundleHistory(submissions);
    }
    return createCurrentBundleSurveyHistory(bundle.getId(), submissions, isCompleted);
  }

  private List<SurveySubmission> findSubmissionsForBundle(
      final Long memberId, final Long bundleId) {
    return surveySubmissionRepository.findByMember_IdAndSurvey_SurveyBundle_Id(memberId, bundleId);
  }

  private SurveyHistoryResponse createCurrentBundleSurveyHistory(
      Long bundleId, List<SurveySubmission> submissions, boolean isSubmittedToday) {
    List<SurveyHistoryDetailResponse> historyDetails =
        submissions.stream().map(BaseEntity::getId).map(SurveyHistoryDetailResponse::of).toList();
    if (isSubmittedToday) {
      return SurveyHistoryResponse.of(bundleId, historyDetails, historyDetails.size(), true);
    }
    return SurveyHistoryResponse.of(bundleId, historyDetails, historyDetails.size() + 1, false);
  }

  private SurveyHistoryResponse getNextBundleHistory(List<SurveySubmission> submissions) {
    Set<Long> bundleIds =
        submissions.stream()
            .map(SurveySubmission::getSurvey)
            .map(Survey::getSurveyBundle)
            .map(SurveyBundle::getId)
            .collect(Collectors.toSet());

    SurveyBundle nextBundle =
        surveyBundleRepository
            .findFirstByIdNotInOrderByIdAsc(bundleIds)
            .orElseThrow(() -> CustomException.NOT_READY_FOR_NEXT_BUNDLE);
    return SurveyHistoryResponse.createNextBundleSurveyHistory(nextBundle.getId());
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

  @Transactional(readOnly = true)
  public SurveySubmissionHistoryResponse getSurveySubmissionHistory(
      final SurveySubmissionHistoryCommand command) {
    return surveySubmissionRepository
        .findByMember_IdAndSurvey_SurveyBundle_Id(command.memberId(), command.bundleId())
        .stream()
        .sorted(Comparator.comparing(SurveySubmission::getSubmittedAt))
        .map(SurveyRecord::of)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(), SurveySubmissionHistoryResponse::new));
  }

  @Transactional(readOnly = true)
  public OnboardingSurveyResponse getOnboardingSurveys() {
    List<OnboardingSurvey> onboardingSurveys = onboardingSurveyRepository.findAll();
    return onboardingSurveys.stream()
        .map(SurveyResponse::of)
        .collect(Collectors.collectingAndThen(Collectors.toList(), OnboardingSurveyResponse::new));
  }
}
