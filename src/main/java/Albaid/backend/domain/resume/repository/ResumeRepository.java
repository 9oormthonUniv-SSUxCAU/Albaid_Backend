package Albaid.backend.domain.resume.repository;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {

    List<Resume> findAllByMemberOrderByCreatedAtDesc(Member member);
}
