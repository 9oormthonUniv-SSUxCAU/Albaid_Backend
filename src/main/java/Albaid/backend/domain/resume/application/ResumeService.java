package Albaid.backend.domain.resume.application;

import Albaid.backend.domain.resume.application.dto.CareerResponseDTO;
import Albaid.backend.domain.resume.application.dto.ResumeRequestDTO;
import Albaid.backend.domain.resume.application.dto.ResumeResponseDTO;
import Albaid.backend.domain.resume.application.dto.SummaryResumeDTO;

import java.util.List;

public interface ResumeService {

    ResumeResponseDTO createResume(ResumeRequestDTO request);

    List<SummaryResumeDTO> getResumeList();

    ResumeResponseDTO getResumeById(Integer id);

    ResumeResponseDTO updateResume(Integer id, ResumeRequestDTO request);

    void deleteResume(Integer id);

    List<CareerResponseDTO> getCareer(Integer id);
}