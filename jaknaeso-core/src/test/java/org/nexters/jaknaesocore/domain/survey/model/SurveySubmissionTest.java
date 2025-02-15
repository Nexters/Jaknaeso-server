package org.nexters.jaknaesocore.domain.survey.model;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SurveySubmissionTest {

  @DisplayName("isSubmittedByDate 메소드는")
  @Nested
  class isSubmittedByDate {
    @DisplayName("제출일이 주어진 날짜와 같으면")
    @Nested
    class submittedDateIsSameAsGivenDate {
      @DisplayName("true를 반환한다")
      @Test
      void itReturnsTrue() {
        // given
        SurveySubmission surveySubmission =
            SurveySubmission.builder().submittedAt(LocalDateTime.of(2026, 2, 6, 0, 0)).build();
        LocalDate date = LocalDate.of(2026, 2, 6);

        // when
        boolean result = surveySubmission.isSubmittedByDate(date);

        // then
        then(result).isTrue();
      }
    }

    @DisplayName("제출일이 주어진 날짜와 다르면")
    @Nested
    class submittedDateIsDifferentFromGivenDate {
      @DisplayName("false를 반환한다")
      @Test
      void itReturnsFalse() {
        // given
        SurveySubmission surveySubmission =
            SurveySubmission.builder().submittedAt(LocalDateTime.of(2026, 2, 6, 0, 0)).build();
        LocalDate date = LocalDate.of(2026, 2, 7);

        // when
        boolean result = surveySubmission.isSubmittedByDate(date);

        // then
        then(result).isFalse();
      }
    }
  }

  @Test
  void 제출한_설문이_온보딩_설문인지_확인한다() {
    // given
    SurveySubmission submission =
        SurveySubmission.builder()
            .survey(
                new OnboardingSurvey(
                    "즐길 수 있는 모든 기회를 찾는다. 자신에게 즐거움을 주는 일이 중요하다.", new SurveyBundle()))
            .build();
    // when
    boolean result = submission.isOnboardingSurvey();
    // then
    then(result).isTrue();
  }
}
