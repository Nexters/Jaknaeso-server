package org.nexters.jaknaesocore.domain.question.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.question.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.question.model.Survey;
import org.nexters.jaknaesocore.domain.question.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.question.repository.BundleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SurveyService {

  private final BundleRepository bundleRepository;
  private final SurveySubmissionReader surveySubmissionReader;

  @Transactional(readOnly = true)
  public SurveyResponse getSurvey(Long bundleId, Long memberId) {
    SurveyBundle bundle =
        bundleRepository
            .findById(bundleId)
            .orElseThrow(() -> new IllegalArgumentException("설문 번들을 찾을 수 없습니다."));

    Set<Long> submittedSurveyIds = surveySubmissionReader.getSubmittedSurveyIds(memberId);

    Survey unSubmittedSurvey = bundle.getUnSubmittedSurvey(submittedSurveyIds);

    return SurveyResponse.of(unSubmittedSurvey);
  }
}
