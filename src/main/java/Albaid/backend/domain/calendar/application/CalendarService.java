package Albaid.backend.domain.calendar.application;

import Albaid.backend.domain.calendar.entity.Calendar;
import Albaid.backend.domain.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public List<Calendar> getCalendarForDate(LocalDate date) {
        return calendarRepository.findAllByDate(date);
    }

    public Calendar createSchedule(Calendar schedule) {
        return calendarRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        calendarRepository.deleteById(id);
    }

}
