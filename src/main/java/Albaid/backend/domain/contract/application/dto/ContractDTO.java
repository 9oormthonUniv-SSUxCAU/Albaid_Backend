package Albaid.backend.domain.contract.application.dto;

import java.time.LocalTime;
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

    @Override
    public boolean isPaidAnnualLeave() {
        return isPaidAnnualLeave;
    }

    @Override
    public boolean isSocialInsurance() {
        return isSocialInsurance;
    }

    @Override
    public boolean isContractDelivery() {
        return isContractDelivery;
    }

    @Override
    public boolean isSafe() {
        return isSafe;
    }

}

