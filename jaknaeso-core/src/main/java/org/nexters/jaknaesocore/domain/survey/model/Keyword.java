package org.nexters.jaknaesocore.domain.survey.model;

public enum Keyword {
  SELF_DIRECTION("자기주도"),
  ADVENTURE("모험"),
  SECURITY("안전"),
  STABILITY("안정"),
  SUCCESS("성공"),
  BENEVOLENCE("박애"),
  UNIVERSALISM("보편주의");

  private final String description;

  Keyword(String description) {
    this.description = description;
  }
}
