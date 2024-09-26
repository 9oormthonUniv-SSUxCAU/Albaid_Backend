package Albaid.backend.domain.contract.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RequestContractDTO(
        String title,
        String workplace,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        LocalTime standardWorkingStartTime,
        LocalTime standardWorkingEndTime,
        List<String> workingDays,
        int hourlyWage,
        String jobDescription,
        boolean isPaidAnnualLeave,
        boolean isSocialInsurance,
        boolean isContractDelivery,
        String memo
) {
}

