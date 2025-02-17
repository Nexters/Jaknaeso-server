package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterTraits {

  @OneToMany(mappedBy = "valueCharacter", cascade = CascadeType.PERSIST)
  @SQLRestriction("deleted_at IS NULL")
  private List<CharacterTrait> values = new ArrayList<>();

  @Builder
  private CharacterTraits(final List<CharacterTrait> values) {
    this.values = values;
  }

  public static CharacterTraits of(final List<CharacterTrait> values) {
    return new CharacterTraits(values);
  }

  public static CharacterTraits empty() {
    return new CharacterTraits();
  }

  public List<CharacterTrait> getByType(final CharacterTraitType type) {
    return values.stream().filter(t -> t.getType().equals(type)).toList();
  }
}
