package org.nexters.jaknaesocore.domain.member.repository;

import org.nexters.jaknaesocore.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
