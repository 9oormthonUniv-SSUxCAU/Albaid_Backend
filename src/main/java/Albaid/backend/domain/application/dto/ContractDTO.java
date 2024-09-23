package Albaid.backend.domain.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ContractDTO(
        String workplace,              // 근무지
        LocalDate contractStartDate,   // 계약 시작일
        LocalDate contractEndDate,     // 계약 종료일
        LocalTime startTime,           // 소정근로 시작 시간
        LocalTime endTime,             // 소정근로 종료 시간
        int workingDaysPerWeek,        // 주당 근무 일자 (주 몇일 근무하는지)
        int hourlyWage,                // 시급
        boolean hasPaidLeave,          // 연차 유급휴가 여부
        boolean hasSocialInsurance,    // 사회보험 적용 여부
        boolean hasContractCopy        // 근로계약서 교부 여부
) {
}
