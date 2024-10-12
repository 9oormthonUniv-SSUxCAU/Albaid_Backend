package Albaid.backend.domain.card.application;

import Albaid.backend.domain.card.application.dto.AlbaCardListDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardResponseDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardSummaryDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardUpdateDto;
import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.card.repository.AlbaCardRepository;
import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.contract.entity.WorkingDays;
import Albaid.backend.domain.contract.repository.ContractRepository;
import Albaid.backend.domain.member.application.MemberService;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.resume.entity.Career;
import Albaid.backend.domain.resume.entity.Resume;
import Albaid.backend.domain.resume.repository.CareerRepository;
import Albaid.backend.domain.resume.repository.ResumeRepository;
import Albaid.backend.global.response.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbaCardServiceImpl implements AlbaCardService {

    private final AlbaCardRepository albaCardRepository;
    private final ContractRepository contractRepository;
    private final MemberService memberService;
    private final ResumeRepository resumeRepository;
    private final CareerRepository careerRepository;

    @Transactional
    @Override
    public AlbaCardResponseDTO createAlbaCard(Integer contractId) {

        Member member = memberService.getCurrentMember();

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Contract not found"));

        AlbaCard albaCard = AlbaCard.builder()
                .contract(contract)
                .isAlive(true)
                .member(member)
                .build();

        AlbaCard savedAlbaCard = albaCardRepository.save(albaCard);

        Resume resume = resumeRepository.findByMemberIdAndIsBaseResume(member.getId(), true);

        Career career = Career.builder()
                .companyName(contract.getWorkplace())
                .startDate(contract.getContractStartDate())
                .endDate(contract.getContractEndDate())
                .resume(resume)
                .build();

        resume.getCareers().add(career);

        careerRepository.save(career);

        WorkReport monthlyReport = calculateMonthlyReport(contract);
        WorkReport totalReport = calculateTotalReport(contract);

        return AlbaCardResponseDTO.of(savedAlbaCard, contract, monthlyReport, totalReport);
    }

    @Override
    public AlbaCardListDTO getAlbaCardList() {
        Member member = memberService.getCurrentMember();
        List<AlbaCard> albaCards = albaCardRepository.findAllByMemberId(member.getId());

        // 현재 달 정보 가져오기
        int currentMonth = LocalDate.now().getMonthValue();

        // AlbaCardSummaryDTO 리스트와 이번 달 월급 총합 계산
        AtomicInteger totalMonthlyWage = new AtomicInteger(0); // 총 월급 계산용

        List<AlbaCardSummaryDTO> summaryDTOList = albaCards.stream()
                .map(albaCard -> {
                    WorkReport monthlyReport = calculateMonthlyReport(albaCard.getContract());
                    totalMonthlyWage.addAndGet(monthlyReport.getCurrentMonthWage()); // 월급을 총합에 더함
                    return AlbaCardSummaryDTO.of(
                            albaCard.getId(),
                            albaCard.getTitle(),
                            albaCard.getWorkplace(),
                            monthlyReport.getCurrentMonthWage()
                    );
                })
                .toList();

        // AlbaCardListDTO로 반환
        return AlbaCardListDTO.of(currentMonth, totalMonthlyWage.get(), summaryDTOList);
    }

    @Override
    public AlbaCardResponseDTO getAlbaCardById(Integer id) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        Contract contract = albaCard.getContract();

        WorkReport monthlyReport = calculateMonthlyReport(contract);
        WorkReport totalReport = calculateTotalReport(contract);

        return AlbaCardResponseDTO.of(albaCard, contract, monthlyReport, totalReport);
    }

    @Transactional
    @Override
    public void deleteAlbaCard(Integer id) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));
        albaCardRepository.delete(albaCard);
    }

    @Transactional
    @Override
    public AlbaCardResponseDTO updateAlbaCard(Integer id, AlbaCardUpdateDto request) {
        AlbaCard albaCard = albaCardRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        if (request.title() != null) {
            albaCard.setTitle(request.title());
        }

        if (request.memo() != null) {
            albaCard.setMemo(request.memo());
        }

        Contract contract = albaCard.getContract();
        WorkReport monthlyReport = calculateMonthlyReport(contract);
        WorkReport totalReport = calculateTotalReport(contract);

        return AlbaCardResponseDTO.of(albaCard, contract, monthlyReport, totalReport);
    }

    @Transactional
    @Override
    public void endAlbaCard(Integer id) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        albaCard.setAlive(false);
    }

    // 이번 달 근로 정보 계산
    public WorkReport calculateMonthlyReport(Contract contract) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDate endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        // 계약 종료일이 이번 달에 포함되면 그 날까지만 계산
        LocalDate contractEndDate = contract.getContractEndDate();
        if (contractEndDate != null && contractEndDate.isBefore(endOfMonth)) {
            endOfMonth = contractEndDate;
        }

        return calculateWorkingDataForPeriod(contract, startOfMonth, endOfMonth, true);
    }

    // 총 근로 정보 계산
    public WorkReport calculateTotalReport(Contract contract) {
        LocalDate contractStartDate = contract.getContractStartDate();
        LocalDate currentDate = LocalDate.now();

        return calculateWorkingDataForPeriod(contract, contractStartDate, currentDate, false);
    }

    // 특정 기간 내 근로 정보 계산 (이번 달/전체 여부에 따라 필드 구분)
    private WorkReport calculateWorkingDataForPeriod(Contract contract, LocalDate startDate, LocalDate endDate, boolean isCurrentMonth) {
        List<WorkingDays> workingDays = contract.getWorkingDays();
        Set<String> workingDaysSet = new HashSet<>();

        // 근무 요일을 HashSet에 저장
        for (WorkingDays workingDay : workingDays) {
            workingDaysSet.add(workingDay.getWorkingDay());
        }

        int workingHoursInPeriod = 0;
        int totalWorkingDays = 0; // 총 출석일 계산용
        int totalWorkingHours = 0; // 총 근로시간 계산용

        LocalTime startWork = contract.getStandardWorkingStartTime();
        LocalTime endWork = contract.getStandardWorkingEndTime();
        int dailyWorkingHours = (int) ChronoUnit.HOURS.between(startWork, endWork); // 하루 근무 시간

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayCode = getDayCode(dayOfWeek); // "MO", "TU" 등의 코드로 변환

            // HashSet에 근무 요일이 있는지 확인
            if (workingDaysSet.contains(dayCode)) {
                workingHoursInPeriod += dailyWorkingHours;
                totalWorkingDays++; // 총 출석일 증가
                totalWorkingHours += dailyWorkingHours; // 총 근로시간 증가
            }
        }

        int hourlyWage = contract.getHourlyWage();
        int wageInPeriod = workingHoursInPeriod * hourlyWage; // 월급 계산

        if (isCurrentMonth) {
            // 이번 달 데이터 리턴
            return new WorkReport(
                    workingHoursInPeriod, // 이번 달 일한 시간
                    wageInPeriod,         // 이번 달 월급
                    0,                    // 총 근로시간은 이후 별도로 계산
                    0,                    // 총 월급은 이후 별도로 계산
                    0                     // 총 출석일은 이후 별도로 계산
            );
        } else {
            // 총 근로시간, 총 출석일수, 총 월급 데이터 리턴
            return new WorkReport(
                    0,                    // 이번 달 정보는 이후 별도로 계산
                    0,                    // 이번 달 정보는 이후 별도로 계산
                    totalWorkingHours,     // 총 근로시간
                    wageInPeriod,          // 총 월급
                    totalWorkingDays        // 총 출석일수
            );
        }
    }

    private String getDayCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MO";
            case TUESDAY -> "TU";
            case WEDNESDAY -> "WE";
            case THURSDAY -> "TH";
            case FRIDAY -> "FR";
            case SATURDAY -> "SA";
            case SUNDAY -> "SU";
        };
    }
}

