package org.nexters.jaknaesocore.domain.character.model;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;

@Getter
public enum CharacterType {
  // TODO: 추후 최종 이름 반영
  SELF_DIRECTION("자율 캐릭터 이름", "자율 캐릭터 설명"),
  ADVENTURE("모험 캐릭터 이름", "모험 캐릭터 설명"),
  SECURITY("튼튼한 보안 전문가", "안전 캐릭터 설명"),
  STABILITY("안정 캐릭터 이름", "안정 캐릭터 설명"),
  SUCCESS("성취를 쫓는 노력가", "성공 캐릭터 설명"),
  BENEVOLENCE("박애 캐릭터 이름", "박애 캐릭터 설명"),
  UNIVERSALISM("절묘한 균형 추구자", "보편 캐릭터 설명");

  private static final Map<Keyword, CharacterType> CHARACTER_TYPE_MAP =
      Stream.of(values()).collect(Collectors.toMap(CharacterType::toKeyword, Function.identity()));

  private final String name;
  private final String description;

  CharacterType(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static CharacterType getByKeyword(final Keyword keyword) {
    return CHARACTER_TYPE_MAP.get(keyword);
  }

  private Keyword toKeyword() {
    return Keyword.valueOf(this.name());
  }
}
