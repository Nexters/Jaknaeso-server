package org.nexters.jaknaesocore.domain.question.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.domain.member.model.Member;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SurveySubmission extends BaseTimeEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_id")
  private Survey survey;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id")
  private SurveyOption selectedOption;

  private String additionalComment;

  @Builder
  private SurveySubmission(
      Member member, Survey survey, SurveyOption selectedOption, String additionalComment) {
    this.member = member;
    this.survey = survey;
    this.selectedOption = selectedOption;
    this.additionalComment = additionalComment;
  }
}
