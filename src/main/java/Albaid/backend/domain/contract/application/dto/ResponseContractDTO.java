package Albaid.backend.domain.contract.application.dto;

import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.contract.entity.WorkingDays;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ResponseContractDTO(
        Integer id,
        String url,
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
    public static ResponseContractDTO of(Contract contract) {
        return new ResponseContractDTO(
                contract.getId(),
                contract.getUrl(),
                contract.getTitle(),
                contract.getWorkplace(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getStandardWorkingStartTime(),
                contract.getStandardWorkingEndTime(),
                contract.getWorkingDays().stream().map(WorkingDays::getWorkingDay).toList(),
                contract.getHourlyWage(),
                contract.getJobDescription(),
                contract.isPaidAnnualLeave(),
                contract.isSocialInsurance(),
                contract.isContractDelivery(),
                contract.getMemo()
        );
    }
}

