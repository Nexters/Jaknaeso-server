package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ValueCharacter extends BaseEntity {

  private String name;

  private String description;

  @Enumerated(EnumType.STRING)
  private Keyword keyword;

  public ValueCharacter(final String name, final String description, final Keyword keyword) {
    this.name = name;
    this.description = description;
    this.keyword = keyword;
  }
}
