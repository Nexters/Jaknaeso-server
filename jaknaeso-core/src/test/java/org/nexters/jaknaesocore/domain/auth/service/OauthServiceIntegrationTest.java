package org.nexters.jaknaesocore.domain.auth.service;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.domain.auth.service.dto.AppleLoginCommand;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccount;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialAccountFixture;
import org.nexters.jaknaesocore.domain.socialaccount.model.SocialProvider;
import org.nexters.jaknaesocore.domain.socialaccount.repository.SocialAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

class OauthServiceIntegrationTest extends IntegrationTest {

  @Autowired OauthService sut;

  @Autowired MemberRepository memberRepository;
  @Autowired SocialAccountRepository socialAccountRepository;

  @AfterEach
  void tearDown() {
    socialAccountRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Nested
  @DisplayName("appleLogin 메소드는 ")
  class appleLogin {

    @Nested
    @DisplayName("id값과 일치하는 유저를 찾지 못하면")
    class whenMemberNotFound {

      @Test
      @DisplayName("회원가입을 진행한다.")
      void shouldSignIn() {
        final String idToken =
            "eyJraWQiOiJBSURPRkZDTzJDM05EUVBGQUJDVEFDT1VDU1ZZQUdTR09ZUEJNVU5KS1FEUVFBQUEyTVE2USIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwMDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIiLCJhdF9oYXNoIjoiUlZfdkZKZnFhdDBGMmFZdHVQUlNlZyIsImF1ZCI6ImNvbS5leGFtcGxlLmFwcGxpbmUud2ViIiwiYXV0aF90aW1lIjoxNjA1MzcxNTU5LCJpc3MiOiJodHRwczovL2lkLmFwcGxlLmNvbSIsImV4cCI6MTYwNTM3NTE1OSwiaWF0IjoxNjA1MzcxNTU5LCJub25jZSI6Ijc5NDc5NTg4MzA1NDQ2OTQzNiIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlfQ.VkmD8KcTtCmN65DPwhAPoOeRuXmsLqnm1z8pWa_qHG3xD2LBJgj9YOZPUKseOlfrOz5e5JgIR1qPdWiL2QFuyjCQZ0PSG0hV1xtQ_yYbVHeqLaID0AgcV8Hxldg9hFvF_jvM8G_mo0S9-D8gOR4kbQ";
        sut.appleLogin(createAppleLoginCommand(idToken, "홍길동"));

        then(memberRepository.findAll()).hasSize(1);
        then(socialAccountRepository.findAll())
            .hasSize(1)
            .extracting("oauthId", "socialProvider")
            .containsExactly(tuple("001234567890123456789012", SocialProvider.APPLE));
      }
    }

    @Nested
    @DisplayName("id값과 일치하는 유저를 찾으면")
    class whenMemberFound {

      @Test
      @DisplayName("토큰을 발행한다.")
      void shouldIssueToken() {
        final Member member = memberRepository.save(Member.create());
        final String oauthId = "001234567890123456789012";
        socialAccountRepository.save(createSocialAccount(member, oauthId, SocialProvider.APPLE));

        final String idToken =
            "eyJraWQiOiJBSURPRkZDTzJDM05EUVBGQUJDVEFDT1VDU1ZZQUdTR09ZUEJNVU5KS1FEUVFBQUEyTVE2USIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwMDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIiLCJhdF9oYXNoIjoiUlZfdkZKZnFhdDBGMmFZdHVQUlNlZyIsImF1ZCI6ImNvbS5leGFtcGxlLmFwcGxpbmUud2ViIiwiYXV0aF90aW1lIjoxNjA1MzcxNTU5LCJpc3MiOiJodHRwczovL2lkLmFwcGxlLmNvbSIsImV4cCI6MTYwNTM3NTE1OSwiaWF0IjoxNjA1MzcxNTU5LCJub25jZSI6Ijc5NDc5NTg4MzA1NDQ2OTQzNiIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlfQ.VkmD8KcTtCmN65DPwhAPoOeRuXmsLqnm1z8pWa_qHG3xD2LBJgj9YOZPUKseOlfrOz5e5JgIR1qPdWiL2QFuyjCQZ0PSG0hV1xtQ_yYbVHeqLaID0AgcV8Hxldg9hFvF_jvM8G_mo0S9-D8gOR4kbQ";
        sut.appleLogin(createAppleLoginCommand(idToken, "홍길동"));

        then(memberRepository.findAll()).hasSize(1);
        then(socialAccountRepository.findAll()).hasSize(1);
      }
    }
  }

  private SocialAccount createSocialAccount(
      final Member member, final String oauthId, final SocialProvider socialProvider) {
    return SocialAccountFixture.builder()
        .member(member)
        .oauthId(oauthId)
        .socialProvider(socialProvider)
        .build();
  }

  private AppleLoginCommand createAppleLoginCommand(String idToken, String name) {
    return new AppleLoginCommand(idToken, name);
  }
}
