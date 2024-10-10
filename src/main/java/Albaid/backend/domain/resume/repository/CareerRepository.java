package Albaid.backend.domain.resume.repository;

import Albaid.backend.domain.resume.entity.Career;
import Albaid.backend.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Integer> {

    List<Career> findByResumeOrderByStartDateDesc(Resume resume);
}