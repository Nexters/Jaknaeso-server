package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmittedAt {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy.MM.dd");

  private LocalDateTime submittedAt;

  @Builder
  private SubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  String getYearMonthDay() {
    return submittedAt.format(DATE_TIME_FORMATTER);
  }

  boolean isSubmittedByDate(LocalDate date) {
    return date.equals(submittedAt.toLocalDate());
  }
}
