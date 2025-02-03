package org.nexters.jaknaesocore.domain.member.service.dto;

import org.nexters.jaknaesocore.domain.member.model.Member;

public record MemberResponse(String name, String email) {

  public static MemberResponse of(final Member member) {
    return new MemberResponse(member.getName(), member.getEmail());
  }
}
