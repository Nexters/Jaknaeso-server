package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class KeywordScore {

  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  @Column(precision = 5, scale = 2)
  private BigDecimal score;

  @Builder
  private KeywordScore(final Keyword keyword, final BigDecimal score) {
    this.keyword = keyword;
    this.score = score;
  }

  public static KeywordScore of(Keyword keyword, BigDecimal score) {
    return KeywordScore.builder().keyword(keyword).score(score).build();
  }
}
