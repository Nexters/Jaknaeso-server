package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.domain.member.model.Member;

@Getter
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

  private String comment;

  @Builder
  private SurveySubmission(
      final Member member,
      final Survey survey,
      final SurveyOption selectedOption,
      final String comment) {
    this.member = member;
    this.survey = survey;
    this.selectedOption = selectedOption;
    this.comment = comment;
  }

  public static SurveySubmission create(
      final Member member,
      final Survey survey,
      final SurveyOption selectedOption,
      final String comment) {
    return SurveySubmission.builder()
        .member(member)
        .survey(survey)
        .selectedOption(selectedOption)
        .comment(comment)
        .build();
  }
}
