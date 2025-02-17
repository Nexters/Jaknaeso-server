package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
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

  @Embedded private CharacterTraits characterTraits = CharacterTraits.empty();

  @Builder
  public ValueCharacter(final String name, final String description, final Keyword keyword) {
    this.name = name;
    this.description = description;
    this.keyword = keyword;
  }

  public List<CharacterTrait> getTraitsByType(final CharacterTraitType type) {
    return characterTraits.getByType(type);
  }
}
