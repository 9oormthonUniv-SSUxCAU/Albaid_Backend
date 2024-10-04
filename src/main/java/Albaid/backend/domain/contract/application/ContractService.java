package Albaid.backend.domain.contract.application;

import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.contract.application.dto.RequestContractDTO;
import Albaid.backend.domain.contract.application.dto.ResponseContractDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContractService {

    ContractDTO extractContractInfo(MultipartFile image);

    ResponseContractDTO saveContract(MultipartFile image, RequestContractDTO request);

    void deleteContract(Integer contractId);

    ResponseContractDTO updateContract(Integer contractId, RequestContractDTO request);

    // 경력 사항 (계약서 데이터) 가져오는 메서드
    List<ContractDTO> getContractsForMember(Long memberId);
}
