package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.resume.entity.Career;
import lombok.Builder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Builder
public record CareerResponseDTO(Integer id, String companyName, String occupation, LocalDate startDate, LocalDate endDate,
                                String totalMonths) {

    public static CareerResponseDTO of(Career career) {
        return CareerResponseDTO.builder()
                .id(career.getId())
                .companyName(career.getCompanyName())
                .occupation(career.getOccupation())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .totalMonths(ChronoUnit.MONTHS.between(career.getStartDate(), career.getEndDate()) + "개월")
                .build();
    }
}