package Albaid.backend.domain.calendar.api;

import Albaid.backend.domain.calendar.application.CalendarService;
import Albaid.backend.domain.calendar.entity.Calendar;
import Albaid.backend.global.response.Response;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/{date}")
    public Response<List<Calendar>> getCalendar(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Calendar> schedules = calendarService.getCalendarForDate(localDate);
        return Response.success(schedules);
    }

    // 일정 생성
    @PostMapping
    public Response<Calendar> createSchedule(@RequestBody Calendar calendar) {
        Calendar createdCalendar = calendarService.createSchedule(calendar);
        return Response.success(createdCalendar);
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public Response<Void> deleteSchedule(@PathVariable Long id) {
        calendarService.deleteSchedule(id);
        return Response.success();
    }
}