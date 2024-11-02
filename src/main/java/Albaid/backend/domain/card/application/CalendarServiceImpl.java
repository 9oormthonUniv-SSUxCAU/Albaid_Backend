package Albaid.backend.domain.card.application;


import Albaid.backend.domain.card.application.dto.AlbaScheduleDTO;
import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.card.repository.AlbaCardRepository;
import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.contract.entity.WorkingDays;
import Albaid.backend.domain.member.application.MemberService;
import Albaid.backend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final AlbaCardRepository albaCardRepository;
    private final MemberService memberService;

    @Override
    public Map<LocalDate, List<AlbaScheduleDTO>> getMonthlySchedule(int year, int month) {
        Member member = memberService.getCurrentMember();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<AlbaCard> albaCards = albaCardRepository.findByMemberAndIsAliveTrue(member);

        // 날짜별로 알바카드를 임시로 저장할 맵
        Map<LocalDate, List<AlbaCard>> tempCalendar = new HashMap<>();

        for (AlbaCard card : albaCards) {
            Contract contract = card.getContract();

            LocalDate contractStart = contract.getContractStartDate();
            LocalDate contractEnd = contract.getContractEndDate() != null ? contract.getContractEndDate() : endOfMonth;

            // 계약 기간이 이번 달과 겹치는지 확인
            if (contractStart.isAfter(endOfMonth) || contractEnd.isBefore(startOfMonth)) {
                continue;
            }

            // 약어를 DayOfWeek 이름과 매핑하여 변환
            List<String> workingDays = contract.getWorkingDays().stream()
                    .map(WorkingDays::getWorkingDay)
                    .map(DAY_OF_WEEK_ABBREVIATIONS::get)  // 약어를 풀 네임으로 변환
                    .toList();

            LocalDate currentDate = startOfMonth;
            while (!currentDate.isAfter(endOfMonth)) {
                if (!currentDate.isBefore(contractStart) && !currentDate.isAfter(contractEnd)) { // 계약 기간 내 날짜
                    if (workingDays.contains(currentDate.getDayOfWeek().name())) {  // 이름으로 비교
                        tempCalendar.computeIfAbsent(currentDate, k -> new ArrayList<>()).add(card);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        // 최종 결과를 담을 Map<LocalDate, List<AlbaScheduleDTO>>
        Map<LocalDate, List<AlbaScheduleDTO>> calendar = new TreeMap<>();

        // 날짜별로 알바카드의 정보를 가져오고 시작 시간 기준으로 정렬
        for (Map.Entry<LocalDate, List<AlbaCard>> entry : tempCalendar.entrySet()) {
            List<AlbaCard> sortedCards = entry.getValue().stream()
                    .sorted(Comparator.comparing(card -> card.getContract().getStandardWorkingStartTime()))
                    .toList();

            // 필요한 정보를 AlbaScheduleDTO로 변환하여 최종 calendar에 저장
            List<AlbaScheduleDTO> cardInfoList = sortedCards.stream()
                    .map(card -> new AlbaScheduleDTO(
                            card.getTitle(),
                            card.getContract().getStandardWorkingStartTime(),
                            card.getContract().getStandardWorkingEndTime()
                    ))
                    .collect(Collectors.toList());

            calendar.put(entry.getKey(), cardInfoList);
        }

        return calendar;
    }

    @Override
    public List<AlbaScheduleDTO> getTodaySchedule() {
        Member member = memberService.getCurrentMember();
        LocalDate today = LocalDate.now();
        List<AlbaCard> albaCards = albaCardRepository.findByMemberAndIsAliveTrue(member);

        // 오늘의 알바카드들을 담을 리스트
        List<AlbaCard> todayCards = new ArrayList<>();

        for (AlbaCard card : albaCards) {
            Contract contract = card.getContract();
            LocalDate contractStart = contract.getContractStartDate();
            LocalDate contractEnd = contract.getContractEndDate() != null ? contract.getContractEndDate() : today;

            // 계약 기간 내에 오늘 날짜가 포함되는지 확인
            if (!today.isBefore(contractStart) && !today.isAfter(contractEnd)) {
                List<String> workingDays = contract.getWorkingDays().stream()
                        .map(WorkingDays::getWorkingDay)
                        .map(DAY_OF_WEEK_ABBREVIATIONS::get)  // 약어를 풀 네임으로 변환
                        .toList();

                // 오늘의 요일이 포함되어 있으면 오늘의 알바카드 리스트에 추가
                if (workingDays.contains(today.getDayOfWeek().name())) {
                    todayCards.add(card);
                }
            }
        }

        // 오늘의 알바카드들을 startTime 기준으로 오름차순 정렬하고 DTO로 변환하여 반환
        return todayCards.stream()
                .sorted(Comparator.comparing(card -> card.getContract().getStandardWorkingStartTime()))
                .map(card -> new AlbaScheduleDTO(
                        card.getTitle(),
                        card.getContract().getStandardWorkingStartTime(),
                        card.getContract().getStandardWorkingEndTime()
                ))
                .collect(Collectors.toList());
    }

    private static final Map<String, String> DAY_OF_WEEK_ABBREVIATIONS = Map.of(
            "MO", "MONDAY",
            "TU", "TUESDAY",
            "WE", "WEDNESDAY",
            "TH", "THURSDAY",
            "FR", "FRIDAY",
            "SA", "SATURDAY",
            "SU", "SUNDAY"
    );
}



