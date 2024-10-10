package Albaid.backend.domain.card.application;

import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.card.application.dto.AlbaCardDTO;
import Albaid.backend.domain.card.repository.AlbaCardRepository;
import Albaid.backend.domain.contract.application.ContractService;
import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.global.response.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbaCardServiceImpl implements AlbaCardService {

    private final AlbaCardRepository albaCardRepository;
    private final ContractService contractService;

    @Override
    public AlbaCardDTO getAlbaCardById(Long id) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        ContractDTO contract = contractService.getContractForCard(albaCard.getContract().getId());

        LocalTime startLocalTime = LocalTime.parse(contract.standardWorkingStartTime());
        LocalTime endLocalTime = LocalTime.parse(contract.standardWorkingEndTime());

        int totalWorkedHoursThisMonth = calculateWorkedHoursThisMonth(contract.workingDays(), LocalDate.now(), startLocalTime, endLocalTime);
        int totalWageThisMonth = totalWorkedHoursThisMonth * contract.hourlyWage();
        int totalWorkDays = contract.workingDays().size();
        int totalWorkedHours = calculateTotalWorkedHours(contract.workingDays(), startLocalTime, endLocalTime);
        int totalWage = totalWorkedHours * contract.hourlyWage();

        return AlbaCardDTO.of(albaCard, contract, totalWorkedHoursThisMonth, totalWageThisMonth, totalWorkedHours, totalWorkDays, totalWage);
    }

    private int calculateWorkedHoursThisMonth(List<String> workingDays, LocalDate currentMonth, LocalTime startTime, LocalTime endTime) {
        return workingDays.stream()
                .filter(day -> LocalDate.parse(day).getMonth().equals(currentMonth.getMonth()))
                .mapToInt(day -> (int) Duration.between(startTime, endTime).toHours())
                .sum();
    }

    private int calculateTotalWorkedHours(List<String> workingDays, LocalTime startTime, LocalTime endTime) {
        return workingDays.stream()
                .mapToInt(day -> (int) Duration.between(startTime, endTime).toHours())
                .sum();
    }

    private Contract contractDtoToEntity(ContractDTO contractDTO) {
        return Contract.builder()
                .workplace(contractDTO.workplace())
                .contractStartDate(LocalDate.parse(contractDTO.contractStartDate()))
                .contractEndDate(LocalDate.parse(contractDTO.contractEndDate()))
                .standardWorkingStartTime(LocalTime.parse(contractDTO.standardWorkingStartTime()))
                .standardWorkingEndTime(LocalTime.parse(contractDTO.standardWorkingEndTime()))
                .hourlyWage(contractDTO.hourlyWage())
                .jobDescription(contractDTO.jobDescription())
                .isPaidAnnualLeave(contractDTO.isPaidAnnualLeave())
                .isSocialInsurance(contractDTO.isSocialInsurance())
                .isContractDelivery(contractDTO.isContractDelivery())
                .isSafe(contractDTO.isSafe())
                .build();
    }


    @Override
    public AlbaCardDTO createAlbaCard(AlbaCardDTO albaCardDto) {

        AlbaCard albaCard = AlbaCard.builder()
                .title(albaCardDto.getTitle())
                .totalWorkedHoursThisMonth(albaCardDto.getTotalWorkedHoursThisMonth())  // Include other fields expected in the builder
                .totalWageThisMonth(albaCardDto.getTotalWageThisMonth())
                .totalWorkedHours(albaCardDto.getTotalWorkedHours())
                .totalWorkDays(albaCardDto.getTotalWorkDays())
                .totalWage(albaCardDto.getTotalWage())
                .memo(albaCardDto.getMemo())
                .contract(contractDtoToEntity(albaCardDto.getContract()))
                .build();

        albaCardRepository.save(albaCard);
        return AlbaCardDTO.of(albaCard, null, 0, 0, 0, 0, 0);
    }
    @Override
    public AlbaCardDTO updateAlbaCard(Long id, AlbaCardDTO albaCardDto) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));

        albaCard.setTitle(albaCardDto.getTitle());
        //albaCard.setWorkplace(albaCardDto.getWorkplace());

        Contract contract = albaCard.getContract();
        contract.setWorkplace(albaCardDto.getWorkplace());
        contract.setContractStartDate(LocalDate.parse(albaCardDto.getContractStartDate()));
        contract.setContractEndDate(LocalDate.parse(albaCardDto.getContractEndDate()));

        albaCardRepository.save(albaCard);
        return AlbaCardDTO.of(albaCard, null, 0, 0, 0, 0, 0);
    }

    @Override
    public void deleteAlbaCard(Long id) {
        AlbaCard albaCard = albaCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "AlbaCard not found"));
        albaCardRepository.delete(albaCard);
    }

}

