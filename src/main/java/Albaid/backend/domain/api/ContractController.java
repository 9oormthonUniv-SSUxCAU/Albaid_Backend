package Albaid.backend.domain.api;

import Albaid.backend.domain.application.OcrService;
import Albaid.backend.domain.application.dto.ContractDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final OcrService ocrService;

    @PostMapping(value = "/upload", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ContractDTO save(@RequestPart MultipartFile contractImage) throws Exception {
        return ocrService.extractContractInfo(contractImage);
    }
}
