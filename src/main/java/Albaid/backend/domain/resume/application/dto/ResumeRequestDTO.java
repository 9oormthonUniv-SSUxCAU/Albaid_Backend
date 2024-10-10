package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.resume.entity.EducationLevel;
import Albaid.backend.domain.resume.entity.Resume;
import lombok.Builder;

import java.util.List;

@Builder
public record ResumeRequestDTO(String title, String summary,
                               String phone, String address, String email,
                               EducationLevel finalEducation, String desiredLocation,
                               String desiredJob, List<CareerRequestDTO> careers, String qualifications) {

    public static Resume toEntity(ResumeRequestDTO resumeDto, Member member) {
        return Resume.builder()
                .title(resumeDto.title())
                .summary(resumeDto.summary())
                .phone(resumeDto.phone())
                .address(resumeDto.address())
                .email(resumeDto.email())
                .finalEducation(resumeDto.finalEducation())
                .qualifications(resumeDto.qualifications())
                .desiredJob(resumeDto.desiredJob())
                .desiredLocation(resumeDto.desiredLocation())
                .careers(resumeDto.careers().stream().map(CareerRequestDTO::toEntity).toList())
                .member(member)
                .build();
    }
}


