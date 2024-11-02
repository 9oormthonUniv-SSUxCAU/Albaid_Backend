package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.resume.entity.Career;
import lombok.Builder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Builder
public record CareerResponseDTO(Integer id, String companyName, LocalDate startDate, LocalDate endDate,
                                String totalMonths) {

    public static CareerResponseDTO of(Career career) {
        String totalMonths;
        if (career.getStartDate() != null && career.getEndDate() != null) {
            totalMonths = ChronoUnit.MONTHS.between(career.getStartDate(), career.getEndDate()) + "개월";
        } else {
            totalMonths = "";
        }

        return CareerResponseDTO.builder()
                .id(career.getId())
                .companyName(career.getCompanyName())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .totalMonths(totalMonths)
                .build();
    }
}

