package Albaid.backend.domain.contract.api;

import Albaid.backend.domain.contract.application.ContractService;
import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.contract.application.dto.ContractListDTO;
import Albaid.backend.domain.contract.application.dto.RequestContractDTO;
import Albaid.backend.domain.contract.application.dto.ResponseContractDTO;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public Response<List<ContractListDTO>> getListContract() {
        List<ContractListDTO> contractList = contractService.getContractList();
        return Response.success(contractList);
    }

    @GetMapping("/{contractId}")
    public Response<ResponseContractDTO> getContract(@PathVariable Integer contractId) {
        ResponseContractDTO contract = contractService.getContract(contractId);
        return Response.success(contract);
    }

    @PostMapping(value = "/upload", consumes = {MULTIPART_FORM_DATA_VALUE})
    public Response<ContractDTO> uploadContractImage(@RequestPart MultipartFile contractImage) {
        ContractDTO contractInfo = contractService.extractContractInfo(contractImage);
        return Response.success(contractInfo);
    }

    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE})
    public Response<ResponseContractDTO> saveContract(@RequestPart MultipartFile contractImage,
                                                      @RequestPart RequestContractDTO request) {
        ResponseContractDTO savedContract = contractService.saveContract(contractImage, request);
        return Response.success(savedContract);
    }

    @PutMapping("/{contractId}")
    public Response<ResponseContractDTO> updateContract(@PathVariable Integer contractId,
                                                        @RequestBody RequestContractDTO request) {
        ResponseContractDTO updatedContract = contractService.updateContract(contractId, request);
        return Response.success(updatedContract);
    }

    @DeleteMapping("/{contractId}")
    public Response<Void> deleteContract(@PathVariable Integer contractId) {
        contractService.deleteContract(contractId);
        return Response.success();
    }
}

