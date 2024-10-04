package Albaid.backend.domain.resume.application;

import Albaid.backend.domain.Career.application.dto.CareerDTO;
import Albaid.backend.domain.resume.application.dto.ResumeDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ResumeService {

    ResumeDTO getResumeById(Long id);

    ResumeDTO createResume(ResumeDTO resumeDto);

    String generateShareLink(Long id);

    ResumeDTO updateResume(Long id, ResumeDTO resumeDto);

    ResumeDTO addCareer(Long id,CareerDTO careerDto);

    void deleteResume(Long id);
}
