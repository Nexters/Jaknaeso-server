package org.nexters.jaknaesocore.domain.member.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.model.SocialAccount;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.member.repository.SocialAccountRepository;
import org.nexters.jaknaesocore.domain.member.repository.WithdrawnMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends IntegrationTest {

  @Autowired MemberService sut;

  @Autowired MemberRepository memberRepository;
  @Autowired WithdrawnMemberRepository withdrawnMemberRepository;
  @Autowired SocialAccountRepository socialAccountRepository;

  @AfterEach
  void tearDown() {
    socialAccountRepository.deleteAllInBatch();
    withdrawnMemberRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Test
  void 멤버를_삭제한다() {
    final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
    final SocialAccount account =
        socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId", member));

    sut.deleteMember(member.getId());

    assertAll(
        () -> then(memberRepository.findByIdAndDeletedAtIsNull(member.getId())).isEmpty(),
        () -> then(socialAccountRepository.findByIdAndDeletedAtIsNull(account.getId())).isEmpty(),
        () -> then(withdrawnMemberRepository.findAll()).hasSize(1));
  }
}
