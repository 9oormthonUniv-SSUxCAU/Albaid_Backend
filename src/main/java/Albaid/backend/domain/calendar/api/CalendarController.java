package Albaid.backend.domain.calendar.api;

import Albaid.backend.domain.calendar.application.CalendarService;
import Albaid.backend.domain.calendar.entity.Calendar;
import Albaid.backend.global.response.Response;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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

    // 오늘의 알바 조회
    @GetMapping("/today")
    public Response<List<Calendar>> getTodayAlba() {
        return Response.success(calendarService.findTodayAlba());
    }

    // 알바 일정 수정
    @PutMapping("/update/{calendarId}")
    public Response<Void> updateAlbaSchedule(@PathVariable Integer calendarId, @RequestParam LocalTime newStartTime, @RequestParam LocalTime newEndTime) {
        calendarService.updateAlbaSchedule(calendarId, newStartTime, newEndTime);
        return Response.success();
    }

    // 알바 일정 삭제
    @DeleteMapping("/delete/{calendarId}")
    public Response<Void> deleteAlbaSchedule(@PathVariable Integer calendarId) {
        calendarService.deleteAlbaSchedule(calendarId);
        return Response.success();
    }

}

