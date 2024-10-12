package Albaid.backend.domain.card.application.dto;

import java.util.List;

public record AlbaCardListDTO(int currentMonth, int totalMonthlyWage, List<AlbaCardSummaryDTO> albaCardSummaryDTOList) {
    public static AlbaCardListDTO of(int currentMonth, int totalMonthlyWage, List<AlbaCardSummaryDTO> albaCardSummaryDTOList) {
        return new AlbaCardListDTO(
                currentMonth,
                totalMonthlyWage,
                albaCardSummaryDTOList
        );
    }
}
