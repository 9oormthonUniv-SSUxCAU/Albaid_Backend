package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.career.application.dto.CareerDTO;
import Albaid.backend.domain.resume.entity.Resume;
import Albaid.backend.global.enums.EducationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {

    private Long id;
    private String name;
    private String title;
    private String summary;
    private String phone;
    private String email;
    private String address;
    private EducationLevel finalEducation;
    private String desiredLocation;
    private String desiredJob;
    private String totalCareerDuration;
    private Integer memberId;
    private List<CareerDTO> careers;

    public static ResumeDTO of(Resume resume, List<CareerDTO> careers) {
        return ResumeDTO.builder()
                .id(resume.getId())
                .name(resume.getName())
                .title(resume.getTitle())
                .summary(resume.getSummary())
                .phone(resume.getPhone())
                .email(resume.getEmail())
                .address(resume.getAddress())
                .finalEducation(resume.getFinalEducation())
                .careers(careers)
                .build();
    }
}
