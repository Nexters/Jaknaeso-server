package org.nexters.jaknaesocore.domain.survey.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class KeywordScore {

  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  private double score;

  @Builder
  private KeywordScore(Keyword keyword, double score) {
    this.keyword = keyword;
    this.score = score;
  }
}
