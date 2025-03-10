package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.common.support.service.LocalDateTimeHolder;
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

  private String retrospective;

  @Embedded private SubmittedAt submittedAt;

  @Builder
  private SurveySubmission(
      Member member,
      Survey survey,
      SurveyOption selectedOption,
      String retrospective,
      LocalDateTime submittedAt) {
    this.member = member;
    this.survey = survey;
    this.selectedOption = selectedOption;
    this.retrospective = retrospective;
    this.submittedAt = SubmittedAt.builder().submittedAt(submittedAt).build();
  }

  public static SurveySubmission create(
      final Member member,
      final Survey survey,
      final SurveyOption selectedOption,
      final String retrospective,
      final LocalDateTimeHolder dateTimeHolder) {
    return SurveySubmission.builder()
        .member(member)
        .survey(survey)
        .selectedOption(selectedOption)
        .retrospective(retrospective)
        .submittedAt(dateTimeHolder.now())
        .build();
  }

  public boolean isSubmittedByDate(LocalDate date) {
    return submittedAt.isSubmittedByDate(date);
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt.getSubmittedAt();
  }

  public String getYearMonthDay() {
    return submittedAt.getYearMonthDay();
  }

  public boolean isOnboardingSurvey() {
    return survey.getSurveyType().equals(SurveyType.ONBOARDING);
  }
}
