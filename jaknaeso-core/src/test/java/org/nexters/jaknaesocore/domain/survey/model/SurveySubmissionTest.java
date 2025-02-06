package org.nexters.jaknaesocore.domain.survey.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.assertj.core.api.BDDAssertions;
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
        BDDAssertions.then(result).isTrue();
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
        BDDAssertions.then(result).isFalse();
      }
    }
  }
}
