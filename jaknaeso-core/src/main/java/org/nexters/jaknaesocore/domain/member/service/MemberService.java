package org.nexters.jaknaesocore.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.model.WithdrawnMember;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.member.repository.WithdrawnMemberRepository;
import org.nexters.jaknaesocore.domain.member.service.dto.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final WithdrawnMemberRepository withdrawnMemberRepository;

  @Transactional
  public void deleteMember(final Long memberId) {
    final Member member = memberRepository.findMember(memberId);
    withdrawnMemberRepository.save(WithdrawnMember.of(member));

    member.softDelete();
  }

  @Transactional(readOnly = true)
  public MemberResponse getMember(final Long memberId) {
    final Member member = memberRepository.findMember(memberId);
    return MemberResponse.of(member);
  }
}
