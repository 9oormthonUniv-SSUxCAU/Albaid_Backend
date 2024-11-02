package Albaid.backend.domain.card.api;

import Albaid.backend.domain.card.application.CalendarService;
import Albaid.backend.domain.card.application.dto.AlbaScheduleDTO;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    public Response<Map<LocalDate, List<AlbaScheduleDTO>>> getMonthlySchedule(int year, int month) {
        Map<LocalDate, List<AlbaScheduleDTO>> monthlySchedule = calendarService.getMonthlySchedule(year, month);
        return Response.success(monthlySchedule);
    }

    @GetMapping("/today")
    public Response<List<AlbaScheduleDTO>> getTodaySchedule() {
        List<AlbaScheduleDTO> todaySchedule = calendarService.getTodaySchedule();
        return Response.success(todaySchedule);
    }
}
