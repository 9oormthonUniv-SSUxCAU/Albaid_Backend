package Albaid.backend.domain.member.repository;

import Albaid.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Member findByProviderId(String providerId);
}
