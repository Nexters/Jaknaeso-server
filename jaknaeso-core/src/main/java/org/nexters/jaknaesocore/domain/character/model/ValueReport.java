package org.nexters.jaknaesocore.domain.character.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ValueReport {

  @JsonProperty("keyword")
  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  @JsonProperty("percentage")
  private BigDecimal percentage;

  private ValueReport(final Keyword keyword, final BigDecimal percentage) {
    this.keyword = keyword;
    this.percentage = percentage;
  }

  public static ValueReport of(final Keyword keyword, final BigDecimal percentage) {
    return new ValueReport(keyword, percentage);
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
    return Objects.equals(keyword, that.keyword) && percentage.compareTo(that.percentage) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyword, percentage.setScale(2, RoundingMode.HALF_UP));
  }

  @Override
  public String toString() {
    return "ValueReport{" + "keyword=" + keyword + ",percentage=" + percentage + "}";
  }
}
