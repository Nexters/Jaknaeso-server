package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ValueReport extends BaseTimeEntity {

  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  private BigDecimal percentage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "character_record_id")
  private CharacterRecord characterRecord;

  private ValueReport(final Keyword keyword, final BigDecimal percentage) {
    this.keyword = keyword;
    this.percentage = percentage;
  }

  public static ValueReport of(final Keyword keyword, final ScaledBigDecimal percentage) {
    return new ValueReport(keyword, percentage.getValue());
  }

  public void updateCharacterRecord(final CharacterRecord characterRecord) {
    this.characterRecord = characterRecord;
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
    return keyword == that.keyword && Objects.equals(percentage, that.percentage);
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
