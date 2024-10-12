package Albaid.backend.domain.card.application.dto;

import Albaid.backend.domain.card.application.WorkReport;
import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.contract.entity.WorkingDays;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AlbaCardResponseDTO(
        Integer id,
        Integer contractId,
        String title,
        String workplace,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        LocalTime standardWorkingStartTime,
        LocalTime standardWorkingEndTime,
        List<String> workingDays,
        int hourlyWage,
        String jobDescription,
        int monthlyWorkingHours,
        int monthlyWage,
        int totalWorkingHours,
        int totalAttendance,
        int totalWage,
        String memo,
        boolean isAlive
) {

    public static AlbaCardResponseDTO of(AlbaCard card, Contract contract, WorkReport monnthlyWorkReport, WorkReport totalWorkReport) {
        return new AlbaCardResponseDTO(
                card.getId(),
                contract.getId(),
                card.getTitle(),
                card.getWorkplace(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getStandardWorkingStartTime(),
                contract.getStandardWorkingEndTime(),
                contract.getWorkingDays().stream().map(WorkingDays::getWorkingDay).toList(),
                contract.getHourlyWage(),
                contract.getJobDescription(),
                monnthlyWorkReport.getCurrentMonthWorkingHours(),
                monnthlyWorkReport.getCurrentMonthWage(),
                totalWorkReport.getTotalWorkingHours(),
                totalWorkReport.getTotalWorkingDays(),
                totalWorkReport.getTotalWage(),
                card.getMemo(),
                card.isAlive()
        );
    }
}


