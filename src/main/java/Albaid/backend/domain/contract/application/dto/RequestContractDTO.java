package Albaid.backend.domain.contract.application.dto;

import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.member.entity.Member;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    public Contract toContract(Member member, String url) {
        return Contract.builder()
                .title(this.title)
                .url(url)
                .workplace(this.workplace)
                .contractStartDate(this.contractStartDate)
                .contractEndDate(this.contractEndDate)
                .standardWorkingStartTime(this.standardWorkingStartTime)
                .standardWorkingEndTime(this.standardWorkingEndTime)
                .hourlyWage(this.hourlyWage)
                .jobDescription(this.jobDescription)
                .isPaidAnnualLeave(this.isPaidAnnualLeave)
                .isSocialInsurance(this.isSocialInsurance)
                .isContractDelivery(this.isContractDelivery)
                .memo(this.memo)
                .workingDays(new ArrayList<>())
                .member(member)
                .build();
    }
}

