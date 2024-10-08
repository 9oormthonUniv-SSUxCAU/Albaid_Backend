package Albaid.backend.domain.career.application.dto;

import Albaid.backend.domain.career.entity.Career;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {

    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;

    public static CareerDTO of(Career career) {
        return CareerDTO.builder()
                .companyName(career.getCompanyName())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .build();
    }
}
