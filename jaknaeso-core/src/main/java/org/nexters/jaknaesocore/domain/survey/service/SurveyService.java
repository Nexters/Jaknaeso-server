package org.nexters.jaknaesocore.domain.survey.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryDetailResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.model.Survey;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubscriptions;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SurveyService {

  private static final int DEFAULT_SIZE_OF_BUNDLE = 15;
  private final SurveyBundleRepository surveyBundleRepository;
  private final SurveySubmissionRepository surveySubmissionRepository;

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
    Long currentBundleId = latestSubmission.getSurvey().getSurveyBundle().getId();
    List<SurveySubmission> submissions = findSubmissionsForBundle(memberId, currentBundleId);

    if (hasReachedSubmissionLimit(submissions)) {
      return createNextBundleSurveyHistory(currentBundleId);
    }
    return createCurrentBundleSurveyHistory(currentBundleId, submissions);
  }

  private SurveySubmission findLatestSubmission(Long memberId) {
    return surveySubmissionRepository
        .findTopByMember_IdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId)
        .orElse(null);
  }

  private List<SurveySubmission> findSubmissionsForBundle(Long memberId, Long bundleId) {
    return surveySubmissionRepository.findByMember_IdAndSurvey_SurveyBundle_Id(memberId, bundleId);
  }

  private boolean hasReachedSubmissionLimit(List<SurveySubmission> submissions) {
    return submissions.size() >= DEFAULT_SIZE_OF_BUNDLE;
  }

  private SurveyHistoryResponse createInitialSurveyHistory() {
    return new SurveyHistoryResponse(1L, List.of(), 1);
  }

  private SurveyHistoryResponse createNextBundleSurveyHistory(Long currentBundleId) {
    return new SurveyHistoryResponse(currentBundleId + 1, List.of(), 1);
  }

  private SurveyHistoryResponse createCurrentBundleSurveyHistory(
      Long bundleId, List<SurveySubmission> submissions) {
    List<SurveyHistoryDetailResponse> historyDetails =
        submissions.stream()
            .map(submission -> new SurveyHistoryDetailResponse(submission.getId()))
            .toList();

    return new SurveyHistoryResponse(bundleId, historyDetails, submissions.size() + 1);
  }
}
