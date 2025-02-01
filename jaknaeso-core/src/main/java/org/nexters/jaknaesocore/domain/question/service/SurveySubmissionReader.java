package org.nexters.jaknaesocore.domain.question.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.domain.question.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.question.repository.SurveySubmissionRepository;
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
