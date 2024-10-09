package Albaid.backend.domain.contract.application;

import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.contract.application.dto.ContractListDTO;
import Albaid.backend.domain.contract.application.dto.RequestContractDTO;
import Albaid.backend.domain.contract.application.dto.ResponseContractDTO;
import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.contract.entity.WorkingDays;
import Albaid.backend.domain.contract.repository.ContractRepository;
import Albaid.backend.domain.member.application.MemberService;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.util.ai.GptService;
import Albaid.backend.global.util.ai.OcrService;
import Albaid.backend.global.util.s3.S3ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Albaid.backend.global.response.ErrorCode.INTERNAL_SERVER_ERROR;
import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    private final MemberService memberService;
    private final OcrService ocrService;
    private final GptService gptService;
    private final S3ImageService s3ImageService;
    private final ObjectMapper objectMapper;

    @Override
    public List<ContractListDTO> getContractList() {
        Member member = memberService.getCurrentMember();
        return contractRepository.findContractsByMemberId(member.getId())
                .stream()
                .map(ContractListDTO::of)
                .toList();
    }

    @Override
    public ResponseContractDTO getContract(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "계약서를 찾을 수 없습니다."));
        return ResponseContractDTO.of(contract);
    }

    @Override
    public ContractDTO extractContractInfo(MultipartFile image) {
        try {
            String text = ocrService.extractTextFromImage(image);
            String contractInfo = gptService.extractContractInfoFromText(text);
            return objectMapper.readValue(contractInfo, ContractDTO.class);
        } catch (Exception e) {
            throw new CustomException(INTERNAL_SERVER_ERROR, "계약서 정보 추출 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    @Override
    public ResponseContractDTO saveContract(MultipartFile image, RequestContractDTO request) {
        Member member = memberService.getCurrentMember();

        String url = s3ImageService.upload("contract", List.of(image)).get(0);

        Contract contract = Contract.builder()
                .title(request.title())
                .url(url)
                .workplace(request.workplace())
                .contractStartDate(request.contractStartDate())
                .contractEndDate(request.contractEndDate())
                .standardWorkingStartTime(request.standardWorkingStartTime())
                .standardWorkingEndTime(request.standardWorkingEndTime())
                .hourlyWage(request.hourlyWage())
                .jobDescription(request.jobDescription())
                .isPaidAnnualLeave(request.isPaidAnnualLeave())
                .isSocialInsurance(request.isSocialInsurance())
                .isContractDelivery(request.isContractDelivery())
                .memo(request.memo())
                .workingDays(new ArrayList<>())
                .member(member)
                .build();

        request.workingDays().forEach(day -> contract.addWorkingDay(new WorkingDays(day, contract)));

        Contract savedContract = contractRepository.save(contract);
        return ResponseContractDTO.of(savedContract);
    }

    @Transactional
    @Override
    public ResponseContractDTO updateContract(Integer contractId, RequestContractDTO request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "계약서를 찾을 수 없습니다."));

        contract.update(request);

        return ResponseContractDTO.of(contract);
    }


    @Transactional
    @Override
    public void deleteContract(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "계약서를 찾을 수 없습니다."));
        s3ImageService.deleteImagesFromS3(List.of(contract.getUrl()));
        contractRepository.delete(contract);
    }

    @Transactional
    @Override
    public List<ContractDTO> getContractsForMember(Integer memberId) {
        List<Contract> contracts = contractRepository.findByMemberId(memberId);
        return contracts.stream()
                .map(contract -> new ContractDTO(
                        contract.getWorkplace(),
                        contract.getContractStartDate(),
                        contract.getContractEndDate(),
                        contract.getStandardWorkingStartTime(),
                        contract.getStandardWorkingEndTime(),
                        null,
                        contract.getHourlyWage(),
                        contract.getJobDescription(),
                        contract.isPaidAnnualLeave(),
                        contract.isSocialInsurance(),
                        contract.isContractDelivery(),
                        contract.isSafe()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ContractDTO getContractForCard(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Contract not found"));

        return ContractDTO.builder()
                .workplace(contract.getWorkplace())
                .contractStartDate(contract.getContractStartDate())
                .contractEndDate(contract.getContractEndDate())
                .standardWorkingStartTime(contract.getStandardWorkingStartTime())
                .standardWorkingEndTime(contract.getStandardWorkingEndTime())
                .workingDays(contract.getWorkingDays().stream()
                        .map(WorkingDays::getWorkingDay)
                        .collect(Collectors.toList()))
                .hourlyWage(contract.getHourlyWage())
                .jobDescription(contract.getJobDescription())
                .isPaidAnnualLeave(contract.isPaidAnnualLeave())
                .isSocialInsurance(contract.isSocialInsurance())
                .isContractDelivery(contract.isContractDelivery())
                .isSafe(contract.isSafe())
                .build();
    }

}


