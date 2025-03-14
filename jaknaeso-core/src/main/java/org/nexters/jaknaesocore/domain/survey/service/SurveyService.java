package org.nexters.jaknaesocore.domain.survey.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.service.LocalDateTimeHolder;
import org.nexters.jaknaesocore.domain.character.service.CharacterService;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionResult;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionsCommand;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryDetailResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionCommand;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionHistoryCommand;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionHistoryResponse;
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
  private final CharacterService characterService;
  private final LocalDateTimeHolder localDateTimeHolder;

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
        .orElse(null);
    // TODO : 해당 부분 온보딩 작업이 완료되면 주석 orElse 삭제 이후 주석 해제
    //        .orElseThrow(() -> CustomException.NOT_PROCEED_ONBOARDING);
  }

  private SurveyHistoryResponse classifySubmission(SurveySubmission latestSubmission) {
    // TODO: null 일 경우 온보딩 작업 완료되면 삭제
    if (latestSubmission == null) {
      return SurveyHistoryResponse.createInitialBundleSurveyHistory();
    }
    SurveyBundle bundle = latestSubmission.getSurvey().getSurveyBundle();
    List<SurveySubmission> submissions =
        findSubmissionsForBundle(latestSubmission.getMember().getId(), bundle.getId());

    if (latestSubmission.isOnboardingSurvey()) {
      return getNextBundleHistory(submissions);
    }
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

    AtomicInteger indexCounter = new AtomicInteger(1);

    List<SurveyHistoryDetailResponse> historyDetails =
        submissions.stream()
            .map(BaseEntity::getId)
            .map(id -> SurveyHistoryDetailResponse.of(id, indexCounter.getAndIncrement()))
            .toList();
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
            .orElseThrow(() -> CustomException.NO_MORE_SURVEY_BUNDLES);
    return SurveyHistoryResponse.createNextBundleSurveyHistory(nextBundle.getId());
  }

  @Transactional
  public void submitSurvey(SurveySubmissionCommand command) {
    Survey survey =
        surveyRepository
            .findByIdWithSurveyBundle(command.surveyId())
            .orElseThrow(() -> CustomException.SURVEY_NOT_FOUND);
    SurveyOption surveyOption = survey.getOptionById(command.optionId());
    Member member =
        memberRepository
            .findByIdAndDeletedAtIsNull(command.memberId())
            .orElseThrow(() -> CustomException.MEMBER_NOT_FOUND);

    SurveySubmission surveySubmission =
        SurveySubmission.create(
            member, survey, surveyOption, command.comment(), localDateTimeHolder);

    surveySubmissionRepository.save(surveySubmission);

    final SurveyBundle bundle = survey.getSurveyBundle();
    final List<SurveySubmission> submissions =
        surveySubmissionRepository
            .findByMemberIdAndBundleIdAndDeletedAtIsNullWithSurveyAndSurveyBundle(
                member.getId(), bundle.getId());
    if (SurveySubmissions.of(submissions).isFirstSubmitted()) {
      characterService.createCharacter(
          member, bundle, surveySubmission.getSubmittedAt().toLocalDate());
    }
    if (bundle.isAllSubmitted(submissions)) {
      completeCharacter(member, bundle, submissions);
    }
  }

  private void completeCharacter(
      final Member member, final SurveyBundle bundle, final List<SurveySubmission> submissions) {
    final List<Survey> surveys = submissions.stream().map(SurveySubmission::getSurvey).toList();
    List<KeywordScore> keywordScores = extractKeywordScores(surveys);
    final List<KeywordScore> scores = KeywordScores.percentScale(keywordScores);

    characterService.updateCharacter(member, bundle, scores, submissions);
  }

  private List<KeywordScore> extractKeywordScores(List<Survey> surveys) {
    return surveys.stream()
        .flatMap(survey -> survey.getOptions().stream())
        .flatMap(option -> option.getScores().stream())
        .toList();
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

  @Transactional
  public void submitOnboardingSurvey(OnboardingSubmissionsCommand command) {
    Member member = getMember(command.memberId());
    if (member.isCompletedOnboarding()) {
      throw CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE;
    }
    List<Long> surveyIds = extractSurveyIdsBy(command);
    Map<Long, Survey> surveyMap = createSurveyMapBy(surveyIds);

    Map<Survey, SurveyOption> surveyToSelectedOption =
        createSurveyToSelectedOption(command.submissions(), surveyMap);

    List<SurveySubmission> submissions =
        surveySubmissionRepository.saveAll(createSubmissionsBy(surveyToSelectedOption, member));
    member.completeOnboarding(localDateTimeHolder);
    final SurveyBundle onboardingBundle =
        onboardingSurveyRepository
            .findTopBy()
            .orElseThrow(() -> CustomException.SURVEY_NOT_FOUND)
            .getSurveyBundle();
    List<KeywordScore> candidateKeywordScores =
        extractKeywordScores(submissions.stream().map(SurveySubmission::getSurvey).toList());
    List<KeywordScore> percentScaleScores = KeywordScores.percentScale(candidateKeywordScores);

    characterService.createFirstCharacter(
        member, onboardingBundle, percentScaleScores, submissions);
  }

  private Member getMember(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(() -> CustomException.MEMBER_NOT_FOUND);
  }

  private Map<Long, Survey> createSurveyMapBy(List<Long> surveyIds) {
    return surveyRepository.findAllByIdWithOptions(surveyIds).stream()
        .collect(Collectors.toMap(Survey::getId, survey -> survey));
  }

  private List<SurveySubmission> createSubmissionsBy(
      Map<Survey, SurveyOption> surveyToSelectedOption, Member member) {
    return surveyToSelectedOption.entrySet().stream()
        .map(
            entry ->
                SurveySubmission.create(
                    member, entry.getKey(), entry.getValue(), null, localDateTimeHolder))
        .toList();
  }

  private List<Long> extractSurveyIdsBy(OnboardingSubmissionsCommand command) {
    return command.submissions().stream()
        .map(OnboardingSubmissionResult::surveyId)
        .distinct()
        .toList();
  }

  private Map<Survey, SurveyOption> createSurveyToSelectedOption(
      List<OnboardingSubmissionResult> submissionResults, Map<Long, Survey> surveyMap) {
    Map<Survey, SurveyOption> surveyOptionMap =
        submissionResults.stream()
            .distinct()
            .map(
                submission -> {
                  Survey survey = surveyMap.get(submission.surveyId());
                  if (survey == null) {
                    throw CustomException.SURVEY_NOT_FOUND;
                  }
                  SurveyOption option = survey.getOptionById(submission.optionId());
                  return Map.entry(survey, option);
                })
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (existingOption, newOption) -> newOption));
    return surveyOptionMap;
  }
}
