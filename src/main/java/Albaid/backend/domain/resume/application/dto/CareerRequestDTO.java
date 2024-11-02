package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.resume.entity.Career;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CareerRequestDTO(String companyName, LocalDate startDate, LocalDate endDate
) {
    public static Career toEntity(CareerRequestDTO careerDto) {
        return Career.builder()
                .companyName(careerDto.companyName())
                .startDate(careerDto.startDate())
                .endDate(careerDto.endDate())
                .build();
    }
}
