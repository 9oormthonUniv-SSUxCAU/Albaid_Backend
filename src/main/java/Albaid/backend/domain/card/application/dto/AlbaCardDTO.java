package Albaid.backend.domain.card.application.dto;

import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.contract.application.dto.ContractDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbaCardDTO {

    private Integer id;
    private String title;
    private String workplace;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private ContractDTO contract;

    private int totalWorkedHoursThisMonth;
    private int totalWageThisMonth;
    private int totalWorkedHours;
    private int totalWorkDays;
    private int totalWage;
    private String memo;

    public static AlbaCardDTO of(AlbaCard card, ContractDTO contract, int totalWorkedHoursThisMonth, int totalWageThisMonth, int totalWorkedHours, int totalWorkDays, int totalWage) {
        return AlbaCardDTO.builder()
                .id(card.getId())
                .title(card.getTitle())
                .workplace(card.getWorkplace())
                .contractStartDate(card.getContractStartDate())
                .contractEndDate(card.getContractEndDate())
                .contract(contract)
                .totalWorkedHoursThisMonth(totalWorkedHoursThisMonth)
                .totalWageThisMonth(totalWageThisMonth)
                .totalWorkedHours(totalWorkedHours)
                .totalWorkDays(totalWorkDays)
                .totalWage(totalWage)
                .memo(card.getMemo())
                .build();
    }
}
