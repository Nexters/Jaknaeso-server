package org.nexters.jaknaesocore.domain.survey.model;

public enum Keyword {
  SELF_DIRECTION("자기주도"),
  STIMULATION("자극"),
  HEDONISM("쾌락"),
  ACHIEVEMENT("성취"),
  POWER("권력"),
  SECURITY("안전"),
  CONFORMITY("순응"),
  TRADITION("전통"),
  BENEVOLENCE("박애"),
  UNIVERSALISM("보편");

  private final String description;

  Keyword(String description) {
    this.description = description;
  }
}
