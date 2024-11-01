package Albaid.backend.domain.calendar.application;

import Albaid.backend.domain.calendar.entity.Calendar;
import Albaid.backend.domain.calendar.repository.CalendarRepository;
import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.card.repository.AlbaCardRepository;
import Albaid.backend.domain.contract.entity.WorkingDays;
import Albaid.backend.global.response.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final AlbaCardRepository albaCardRepository;

    @Override
    public List<Calendar> getCalendarForDate(LocalDate date) {
        return calendarRepository.findByDate(date);
    }

    @Override
    public void addAlbaCardCalendar(Integer albaCardId) {
        AlbaCard albaCard = albaCardRepository.findById(albaCardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        List<LocalDate> workingDates = findWorkingDatesForMonth(albaCard.getContract().getWorkingDays());
        for (LocalDate date : workingDates) {
            Calendar calendar = new Calendar(albaCard, date);
            calendarRepository.save(calendar);
        }
    }

    @Override
    public List<Calendar> findTodayAlba() {
        LocalDate today = LocalDate.now();
        return calendarRepository.findByDate(today).stream()
                .filter(calendar -> calendar.getStartTime().isBefore(LocalTime.now())
                        && calendar.getEndTime().isAfter(LocalTime.now()))
                .toList();
    }

    @Override
    public void updateAlbaSchedule(Integer calendarId, LocalTime newStartTime, LocalTime newEndTime) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Calendar not found"));
        calendar.setStartTime(newStartTime);
        calendar.setEndTime(newEndTime);
        calendarRepository.save(calendar);
    }

    @Override
    public void deleteAlbaSchedule(Integer calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Calendar not found"));
        calendarRepository.delete(calendar);
    }

    private List<LocalDate> findWorkingDatesForMonth(List<WorkingDays> workingDays) {
        List<LocalDate> workingDates = new ArrayList<>();
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        for (WorkingDays workingDay : workingDays) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(workingDay.getWorkingDay().toUpperCase());
            for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
                LocalDate date = currentMonth.atDay(day);
                if (date.getDayOfWeek() == dayOfWeek) {
                    workingDates.add(date);
                }
            }
        }
        return workingDates;
    }
}
