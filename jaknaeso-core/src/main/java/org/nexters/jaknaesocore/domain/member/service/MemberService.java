package org.nexters.jaknaesocore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public void deleteMember(final Long memberId) {
    Member member = memberRepository.findMember(memberId);
    member.softDelete();
  }
}
