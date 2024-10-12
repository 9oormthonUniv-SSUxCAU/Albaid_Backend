package Albaid.backend.domain.calendar.application;

import Albaid.backend.domain.calendar.entity.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CalendarService {
    List<Calendar> getCalendarForDate(LocalDate date);
    void addAlbaCardCalendar(Integer albaCardId);
    List<Calendar> findTodayAlba();
    void updateAlbaSchedule(Integer calendarId, LocalTime newStartTime, LocalTime newEndTime);
    void deleteAlbaSchedule(Integer calendarId);
}

