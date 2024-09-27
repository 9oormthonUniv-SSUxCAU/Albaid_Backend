package Albaid.backend.domain.contract.application;

import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.contract.application.dto.ContractListDTO;
import Albaid.backend.domain.contract.application.dto.RequestContractDTO;
import Albaid.backend.domain.contract.application.dto.ResponseContractDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContractService {

    List<ContractListDTO> getContractList();

    ResponseContractDTO getContract(Integer contractId);

    ContractDTO extractContractInfo(MultipartFile image);

    ResponseContractDTO saveContract(MultipartFile image, RequestContractDTO request);

    void deleteContract(Integer contractId);

    ResponseContractDTO updateContract(Integer contractId, RequestContractDTO request);
}
