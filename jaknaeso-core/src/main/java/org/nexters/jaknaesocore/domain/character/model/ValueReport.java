package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ValueReport {

  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  private BigDecimal percentage;

  private ValueReport(final Keyword keyword, final BigDecimal percentage) {
    this.keyword = keyword;
    this.percentage = percentage;
  }

  public static ValueReport of(final Keyword keyword, final ScaledBigDecimal percentage) {
    return new ValueReport(keyword, percentage.getValue());
  }

  @Override
  public String toString() {
    return "ValueReport{" + "keyword=" + keyword + ",percentage=" + percentage + "}";
  }
}
