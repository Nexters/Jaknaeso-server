package org.nexters.jaknaesocore.domain.member.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.RepositoryTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends RepositoryTest {

  @Autowired MemberRepository memberRepository;

  @DisplayName("아이디에 해당하는 멤버를 삭제한다.")
  @Test
  void deleteMemberSuccess() {
    Member member = memberRepository.save(Member.create());

    memberRepository.deleteMember(member.getId());

    then(memberRepository.findById(member.getId())).isEmpty();
  }

  @DisplayName("아이디에 해당하는 멤버가 없는 경우, MEBMER_NOT_FOUND 예외를 반환하고 삭제에 실패한다.")
  @Test
  void deleteMemberFail() {
    thenThrownBy(() -> memberRepository.deleteMember(1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorType.MEMBER_NOT_FOUND.getMessage());
  }
}
