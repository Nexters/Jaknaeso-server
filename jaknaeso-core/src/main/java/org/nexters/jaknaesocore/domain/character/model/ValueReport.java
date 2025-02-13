package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

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

  public static ValueReport of(final Keyword keyword, final Percentage percentage) {
    return new ValueReport(keyword, percentage.getValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValueReport that = (ValueReport) o;
    return Objects.equals(keyword, that.keyword) && Objects.equals(percentage, that.percentage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyword, percentage);
  }

  @Override
  public String toString() {
    return "ValueReport{" + "keyword=" + keyword + ",percentage=" + percentage + "}";
  }
}
