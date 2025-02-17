package org.nexters.jaknaesocore.domain.character.model;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterTrait extends BaseTimeEntity {

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "value_character_id")
  private ValueCharacter valueCharacter;

  @Getter private String description;

  @Enumerated(EnumType.STRING)
  @Getter
  private CharacterTraitType type;

  @Builder
  private CharacterTrait(
      final ValueCharacter valueCharacter,
      final String description,
      final CharacterTraitType type) {
    this.valueCharacter = valueCharacter;
    this.description = description;
    this.type = type;
  }
}
