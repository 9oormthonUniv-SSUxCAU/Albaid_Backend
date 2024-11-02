package Albaid.backend.domain.card.application;


import Albaid.backend.domain.card.application.dto.AlbaScheduleDTO;
import Albaid.backend.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CalendarService {

    Map<LocalDate, List<AlbaScheduleDTO>> getMonthlySchedule(int year, int month);

    List<AlbaScheduleDTO> getTodaySchedule();
}
