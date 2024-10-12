package Albaid.backend.domain.contract.application;

import Albaid.backend.domain.card.repository.AlbaCardRepository;
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

import java.util.List;

import static Albaid.backend.global.response.ErrorCode.INTERNAL_SERVER_ERROR;
import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final AlbaCardRepository albaCardRepository;

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

        Contract contract = request.toContract(member, url);

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

        // 계약서에 연결된 알바카드 삭제
        albaCardRepository.deleteByContractId(contractId);

        // S3에 있는 이미지 삭제
        s3ImageService.deleteImagesFromS3(List.of(contract.getUrl()));

        // 계약서 삭제
        contractRepository.delete(contract);
    }

    public ContractDTO getContractForCard(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "Contract not found"));

        return new ContractDTO(
                contract.getWorkplace(),
                contract.getContractStartDate().toString(),
                contract.getContractEndDate().toString(),
                contract.getStandardWorkingStartTime().toString(),
                contract.getStandardWorkingEndTime().toString(),
                contract.getWorkingDays().stream().map(WorkingDays::getWorkingDay).toList(),
                contract.getHourlyWage(),
                contract.getJobDescription(),
                contract.isPaidAnnualLeave(),
                contract.isSocialInsurance(),
                contract.isContractDelivery(),
                contract.isSafe()
        );
    }
}


