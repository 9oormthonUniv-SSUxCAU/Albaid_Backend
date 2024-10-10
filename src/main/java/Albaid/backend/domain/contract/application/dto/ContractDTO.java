package Albaid.backend.domain.contract.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {
    private String workplace;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalTime standardWorkingStartTime;
    private LocalTime standardWorkingEndTime;
    private List<String> workingDays;
    private int hourlyWage;
    private String jobDescription;
    private boolean isPaidAnnualLeave;
    private boolean isSocialInsurance;
    private boolean isContractDelivery;
    private boolean isSafe;
}


