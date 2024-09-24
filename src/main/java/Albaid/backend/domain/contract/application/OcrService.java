package Albaid.backend.domain.contract.application;

import Albaid.backend.domain.contract.application.dto.ContractDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GptService gptService;

    @Value("${clova.ocr.api.key}")
    private String OCR_API_KEY;
    @Value("${clova.ocr.api.url}")
    private String OCR_API_URL;

    public ContractDTO extractContractInfo(MultipartFile image) throws Exception {
        int retryCount = 0;
        int maxRetries = 3;
        JSONArray imagesArray = null;

        while (retryCount < maxRetries) {
            // 1. 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", OCR_API_KEY);

            // 2. 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("version", "V2");
            requestBody.put("requestId", UUID.randomUUID().toString());
            requestBody.put("timestamp", System.currentTimeMillis());
            requestBody.put("lang", "ko");

            Map<String, String> imageMap = new HashMap<>();
            imageMap.put("format", getImageFormat(image));
            imageMap.put("data", Base64.getEncoder().encodeToString(image.getBytes()));
            imageMap.put("name", image.getOriginalFilename());

            requestBody.put("images", new Map[]{imageMap});

            // 3. 요청 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 4. OCR API 호출
            ResponseEntity<String> response = restTemplate.exchange(OCR_API_URL, HttpMethod.POST, requestEntity, String.class);

            // 5. 응답 데이터 파싱
            String jsonResponse = response.getBody();

            // 6. inferResult 확인
            JSONObject jsonObject = new JSONObject(jsonResponse);
            imagesArray = jsonObject.getJSONArray("images");
            JSONObject firstImage = imagesArray.getJSONObject(0);
            String inferResult = firstImage.getString("inferResult");

            if (inferResult.equals("SUCCESS")) {
                break;  // OCR 성공 시 반복 종료
            } else {
                retryCount++;
                log.warn("OCR 실패. 재시도 중... (" + retryCount + "/" + maxRetries + ")");
                if (retryCount >= maxRetries) {
                    throw new Exception("OCR 요청이 실패했습니다. 최대 재시도 횟수를 초과했습니다.");
                }
            }
        }

        // 7. OCR 결과에서 텍스트 정보만 추출
        String finalText = OcrResultTextExtractor(imagesArray);
        String contractInfoFromJsonText = gptService.extractContractInfoFromText(finalText);
        System.out.println(contractInfoFromJsonText);
        return objectMapper.readValue(contractInfoFromJsonText, ContractDTO.class);
    }

    private String OcrResultTextExtractor(JSONArray imagesArray) throws JSONException {

        StringBuilder extractedText = new StringBuilder();

        for (int i = 0; i < imagesArray.length(); i++) {
            JSONObject image = imagesArray.getJSONObject(i);
            JSONArray fields = image.getJSONArray("fields");

            // 텍스트 추출
            for (int j = 0; j < fields.length(); j++) {
                JSONObject field = fields.getJSONObject(j);
                String text = field.getString("inferText");
                boolean lineBreak = field.getBoolean("lineBreak");

                extractedText.append(text);
                if (lineBreak) {
                    extractedText.append("\n");  // 줄바꿈 정보 있으면 새로운 줄 추가
                } else {
                    extractedText.append(" ");  // 줄바꿈 없으면 공백 추가
                }
            }
        }

        // 최종 추출된 텍스트 출력
        return extractedText.toString().trim();
    }

    private String getImageFormat(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType != null && contentType.contains("/")) {
            return contentType.split("/")[1]; // 예: "image/jpeg" -> "jpeg"
        }
        return "jpg";  // 기본값으로 jpg
    }
}
