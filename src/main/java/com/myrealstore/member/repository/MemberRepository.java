package com.myrealstore.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myrealstore.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
