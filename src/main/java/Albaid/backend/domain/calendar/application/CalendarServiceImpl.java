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


    // 캘린더 데이터 조회하기
    @Override
    public List<Calendar> getCalendarForDate(LocalDate date) {
        return calendarRepository.findByDate(date);
    }

    // 알바카드를 기준으로 캘린더에 추가하기
    @Override
    public void addAlbaCardCalendar(Integer albaCardId) {
        AlbaCard albaCard = albaCardRepository.findById(albaCardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        // 근무일자를 기준으로 해당 월의 일정 생성
        List<LocalDate> workingDates = findWorkingDatesForMonth(albaCard.getContract().getWorkingDays());
        for (LocalDate date : workingDates) {
            Calendar calendar = new Calendar(albaCard, date,
                    albaCard.getContract().getStandardWorkingStartTime(),
                    albaCard.getContract().getStandardWorkingEndTime());
            calendarRepository.save(calendar);
        }
    }

    // 오늘의 알바 찾기
    @Override
    public List<Calendar> findTodayAlba() {
        LocalDate today = LocalDate.now();
        return calendarRepository.findByDate(today).stream()
                .filter(calendar -> calendar.getStartTime().isBefore(LocalTime.now())
                        && calendar.getEndTime().isAfter(LocalTime.now()))
                .toList();
    }

    //
    public void updateAlbaSchedule(Integer calendarId, LocalTime newStartTime, LocalTime newEndTime) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Calendar not found"));
        calendar.setStartTime(newStartTime);
        calendar.setEndTime(newEndTime);
        calendarRepository.save(calendar);
    }

    // 캘린더에서 일정 삭제
    @Override
    public void deleteAlbaSchedule(Integer calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Calendar not found"));
        calendarRepository.delete(calendar);
    }

    // 근무일에 맞는 날짜 리스트 생성
    private List<LocalDate> findWorkingDatesForMonth(List<WorkingDays> workingDays) {
        List<LocalDate> workingDates = new ArrayList<>();
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        // WorkingDays 객체에서 요일 정보를 추출하여 String으로 변환
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