package org.nexters.jaknaesocore.domain.character.model;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CharacterTraitsTest {

  @Nested
  @DisplayName("getByType 메소드는")
  class getByType {

    @Nested
    @DisplayName("MAIN_TRAIT을 전달받으면, ")
    class whenMainTrait {

      @Test
      @DisplayName("주요 특성 목록만 반환한다.")
      void shouldOnlyReturnMainTraits() {
        final CharacterTraits sut =
            createCharacterTraits(
                CharacterTraitType.MAIN_TRAIT,
                CharacterTraitType.STRENGTH,
                CharacterTraitType.WEAKNESS);

        then(sut.getByType(CharacterTraitType.MAIN_TRAIT))
            .hasSize(1)
            .extracting("type")
            .containsExactly(CharacterTraitType.MAIN_TRAIT);
      }
    }

    @Nested
    @DisplayName("MAIN_TRAIT을 전달 받았는데 목록이 비어있다면, ")
    class whenMainTraitButEmpty {

      @Test
      @DisplayName("빈 배열을 반환한다.")
      void shouldReturnEmptyList() {
        final CharacterTraits sut =
            createCharacterTraits(CharacterTraitType.STRENGTH, CharacterTraitType.WEAKNESS);

        then(sut.getByType(CharacterTraitType.MAIN_TRAIT)).hasSize(0);
      }
    }

    @Nested
    @DisplayName("STRENGTH을 전달받으면, ")
    class whenStrength {

      @Test
      @DisplayName("주요 특성 목록만 반환한다.")
      void shouldOnlyReturnStrengthTraits() {
        final CharacterTraits sut =
            createCharacterTraits(
                CharacterTraitType.MAIN_TRAIT,
                CharacterTraitType.STRENGTH,
                CharacterTraitType.WEAKNESS);

        then(sut.getByType(CharacterTraitType.STRENGTH))
            .hasSize(1)
            .extracting("type")
            .containsExactly(CharacterTraitType.STRENGTH);
      }
    }

    @Nested
    @DisplayName("WEAKNESS을 전달받으면, ")
    class whenWeakness {

      @Test
      @DisplayName("주요 특성 목록만 반환한다.")
      void shouldOnlyReturnWeaknessTraits() {
        final CharacterTraits sut =
            createCharacterTraits(
                CharacterTraitType.MAIN_TRAIT,
                CharacterTraitType.STRENGTH,
                CharacterTraitType.WEAKNESS);

        then(sut.getByType(CharacterTraitType.WEAKNESS))
            .hasSize(1)
            .extracting("type")
            .containsExactly(CharacterTraitType.WEAKNESS);
      }
    }
  }

  private CharacterTrait createCharacterTrait(final CharacterTraitType type) {
    return CharacterTrait.builder().type(type).build();
  }

  private CharacterTraits createCharacterTraits(final CharacterTraitType... type) {
    return CharacterTraits.builder()
        .values(Stream.of(type).map(this::createCharacterTrait).toList())
        .build();
  }
}
