package Albaid.backend.global.util.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcrService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${clova.ocr.api.key}")
    private String OCR_API_KEY;
    @Value("${clova.ocr.api.url}")
    private String OCR_API_URL;

    public String extractTextFromImage(MultipartFile image) throws Exception {
        for (int retryCount = 0; retryCount < 3; retryCount++) {
            try {
                HttpHeaders headers = createHeaders();
                Map<String, Object> requestBody = createRequestBody(image);
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                // OCR API 호출
                ResponseEntity<String> response = restTemplate.exchange(OCR_API_URL, HttpMethod.POST, requestEntity, String.class);

                // 응답 데이터 파싱
                JsonNode imagesArray = objectMapper.readTree(response.getBody()).get("images");
                String inferResult = imagesArray.get(0).get("inferResult").asText();

                if (inferResult.equals("SUCCESS")) {
                    return extractTextFromJson(imagesArray);  // 성공 시 텍스트 반환
                } else {
                    log.warn("OCR 실패. 재시도 중... (" + (retryCount + 1) + "/3)");
                }
            } catch (Exception e) {
                log.error("OCR 요청 중 오류 발생: {}", e.getMessage(), e);
                if (retryCount == 2) throw new Exception("OCR 요청 실패: 최대 재시도 횟수를 초과했습니다.", e);
            }
        }
        throw new Exception("OCR 요청 실패: 최대 재시도 횟수를 초과했습니다.");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", OCR_API_KEY);
        return headers;
    }

    private Map<String, Object> createRequestBody(MultipartFile image) throws Exception {
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
        return requestBody;
    }

    private String extractTextFromJson(JsonNode imagesArray) {
        StringBuilder extractedText = new StringBuilder();
        for (JsonNode image : imagesArray) {
            for (JsonNode field : image.get("fields")) {
                String text = field.get("inferText").asText();
                boolean lineBreak = field.get("lineBreak").asBoolean();
                extractedText.append(text);
                if (lineBreak) {
                    extractedText.append("\n");
                } else {
                    extractedText.append(" ");
                }
            }
        }
        return extractedText.toString().trim();
    }

    private String getImageFormat(MultipartFile image) {
        String contentType = image.getContentType();
        return (contentType != null && contentType.contains("/"))
                ? contentType.split("/")[1]
                : "jpg";  // 기본값은 jpg
    }
}