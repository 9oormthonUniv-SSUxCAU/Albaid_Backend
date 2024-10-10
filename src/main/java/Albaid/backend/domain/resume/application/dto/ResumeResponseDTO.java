package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.resume.entity.EducationLevel;
import Albaid.backend.domain.resume.entity.Resume;
import lombok.Builder;

import java.util.List;

@Builder
public record ResumeResponseDTO(Integer id, String title, String summary,
                                String phone, String address, String email,
                                EducationLevel finalEducation, String desiredLocation,
                                String desiredJob, List<CareerResponseDTO> careers, String qualifications) {

    public static ResumeResponseDTO of(Resume resume) {
        return ResumeResponseDTO.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .summary(resume.getSummary())
                .phone(resume.getPhone())
                .address(resume.getAddress())
                .email(resume.getEmail())
                .finalEducation(resume.getFinalEducation())
                .qualifications(resume.getQualifications())
                .desiredLocation(resume.getDesiredLocation())
                .desiredJob(resume.getDesiredJob())
                .careers(resume.getCareers().stream().map(CareerResponseDTO::of).toList())
                .build();
    }
}
