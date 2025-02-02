package org.nexters.jaknaesocore.domain.member.service;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends IntegrationTest {

  @Autowired MemberService sut;

  @Autowired MemberRepository memberRepository;
  @Autowired SocialAccountRepository socialAccountRepository;

  @AfterEach
  void tearDown() {
    memberRepository.deleteAllInBatch();
    socialAccountRepository.deleteAllInBatch();
  }

  @Test
  void 멤버를_삭제한다() {
    Member member = memberRepository.save(Member.create());

    sut.deleteMember(member.getId());

    thenThrownBy(() -> memberRepository.findMember(member.getId()))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorType.MEMBER_NOT_FOUND.getMessage());
  }

  @Test
  void 멤버_삭제_시_멤버의_소셜_계정도_함께_삭제한다() {
    Member member = memberRepository.save(Member.create());
    SocialAccount account =
        socialAccountRepository.save(SocialAccount.kakaoSignup("oauthId1", member));

    sut.deleteMember(member.getId());

    thenThrownBy(() -> socialAccountRepository.findSocialAccount(account.getId()))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorType.SOCIAL_ACCOUNT_NOT_FOUND.getMessage());
  }
}
