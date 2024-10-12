package Albaid.backend.domain.card.application;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkReport {

    private int currentMonthWorkingHours; // 이번 달 일한 시간
    private int currentMonthWage;         // 이번 달 월급
    private int totalWorkingHours;        // 총 근로시간
    private int totalWage;                // 총 월급
    private int totalWorkingDays;         // 총 출석일수

    public WorkReport(int currentMonthWorkingHours, int currentMonthWage, int totalWorkingHours, int totalWage, int totalWorkingDays) {
        this.currentMonthWorkingHours = currentMonthWorkingHours;
        this.currentMonthWage = currentMonthWage;
        this.totalWorkingHours = totalWorkingHours;
        this.totalWage = totalWage;
        this.totalWorkingDays = totalWorkingDays;
    }
}
