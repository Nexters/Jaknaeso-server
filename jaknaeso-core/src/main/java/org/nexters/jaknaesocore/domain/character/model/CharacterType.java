package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseEntity;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CharacterType extends BaseEntity {

  private String name;

  private String description;

  public CharacterType(final String name, final String description) {
    this.name = name;
    this.description = description;
  }

  public Keyword toKeyword() {
    return Keyword.valueOf(this.name);
  }
}
