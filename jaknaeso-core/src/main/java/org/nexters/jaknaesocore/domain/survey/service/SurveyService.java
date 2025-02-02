package org.nexters.jaknaesocore.domain.survey.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.model.Survey;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SurveyService {

  private final SurveyBundleRepository surveyBundleRepository;
  private final SurveySubmissionReader surveySubmissionReader;

  @Transactional(readOnly = true)
  public SurveyResponse getNextSurvey(Long bundleId, Long memberId) {
    SurveyBundle bundle =
        surveyBundleRepository
            .findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("설문 번들을 찾을 수 없습니다."));

    Set<Long> submittedSurveyIds = surveySubmissionReader.getSubmittedSurveyIds(memberId);

    Survey unSubmittedSurvey = bundle.getUnSubmittedSurvey(submittedSurveyIds);

    return SurveyResponse.of(unSubmittedSurvey);
  }
}
