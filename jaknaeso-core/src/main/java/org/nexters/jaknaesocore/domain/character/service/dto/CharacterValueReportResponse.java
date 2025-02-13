package org.nexters.jaknaesocore.domain.character.service.dto;

import java.math.BigDecimal;
import java.util.List;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

public record CharacterValueReportResponse(List<ValueReportResponse> valueReports) {

  public static CharacterValueReportResponse of(final List<ValueReport> valueReports) {
    return new CharacterValueReportResponse(
        valueReports.stream()
            .map(it -> new ValueReportResponse(it.getKeyword(), it.getPercentage()))
            .toList());
  }

  public record ValueReportResponse(Keyword keyword, BigDecimal percentage) {}
}
