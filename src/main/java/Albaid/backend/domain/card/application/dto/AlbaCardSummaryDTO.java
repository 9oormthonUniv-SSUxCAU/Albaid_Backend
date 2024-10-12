package Albaid.backend.domain.card.application.dto;

public record AlbaCardSummaryDTO(
        Integer id,
        String title,
        String workplace,
        int monthlyWage
) {
    public static AlbaCardSummaryDTO of(Integer id, String title, String workplace, int monthlyWage) {
        return new AlbaCardSummaryDTO(
                id,
                title,
                workplace,
                monthlyWage
        );
    }
}


