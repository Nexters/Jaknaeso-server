package org.nexters.jaknaesocore.domain.survey.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SurveySubmissionReader {

  private final SurveySubmissionRepository surveySubmissionRepository;

  public Set<Long> getSubmittedSurveyIds(Long memberId) {
    return surveySubmissionRepository.findByMember_IdAndDeletedAtIsNull(memberId).stream()
        .map(SurveySubmission::getSurvey)
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }
}
