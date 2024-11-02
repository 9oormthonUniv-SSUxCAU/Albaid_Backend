package Albaid.backend.domain.card.application.dto;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class AlbaScheduleDTO {

    private String title;
    private LocalTime startTime;
    private LocalTime endTime;

    public AlbaScheduleDTO(String title, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

