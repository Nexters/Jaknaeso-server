package org.nexters.jaknaesocore.domain.member.repository;

import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  default void deleteMember(final Long memberId) {
    findById(memberId).orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));
    deleteById(memberId);
  }
}
