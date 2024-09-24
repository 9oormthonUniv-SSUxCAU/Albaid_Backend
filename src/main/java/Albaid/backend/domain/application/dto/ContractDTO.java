package Albaid.backend.domain.application.dto;

import java.util.List;

public record ContractDTO(
        String workplace,
        String contractStartDate,
        String contractEndDate,
        String standardWorkingStartTime,
        String standardWorkingEndTime,
        List<String> workingDays,
        int hourlyWage,
        String jobDescription,
        boolean isPaidAnnualLeave,
        boolean isSocialInsurance,
        boolean isContractDelivery,
        boolean isSafe
) {
}

