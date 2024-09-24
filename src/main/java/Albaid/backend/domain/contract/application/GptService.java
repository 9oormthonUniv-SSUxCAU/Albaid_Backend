package Albaid.backend.domain.contract.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String extractContractInfoFromText(String text) throws Exception {

        log.error("text = {}", text);
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + gptApiKey);

        // 요청 바디 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-0125");
        requestBody.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", getSystemContent());
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", text);
                }}
        });

        // JSON 변환
        String requestJson = objectMapper.writeValueAsString(requestBody);

        // HTTP 요청 전송
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(gptApiUrl, HttpMethod.POST, entity, String.class);

        // 응답 데이터 처리
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());

        return jsonResponse.get("choices").get(0).get("message").get("content").asText();
    }

    private String getSystemContent() {
        return "Please extract the following information from the given Contract text. " +
                "Ensure that the output is in strict JSON format with the appropriate field names and values. " +
                "If any information is not provided or not applicable, return null for that field. " +
                "All boolean values should be returned as true or false. The required fields are:\n\n" +
                "- workplace (string): The name or address of the workplace. If not provided, return null.\n" +
                "- contractStartDate (string): The start date of the Contract in 'YYYY-MM-DD' format. If not provided, return null.\n" +
                "- contractEndDate (string): The end date of the Contract in 'YYYY-MM-DD' format. If not provided, return null.\n" +
                "- standardWorkingStartTime (string): The start time of the standard working hours (e.g., '10:00'). If not provided, return null.\n" +
                "- standardWorkingEndTime (string): The end time of the standard working hours (e.g., '16:00'). If not provided, return null.\n" +
                "- workingDays (array): A list of days of the week the employee is expected to work (e.g., ['Monday', 'Wednesday']). If not provided, return null.\n" +
                "- hourlyWage (number): The hourly wage in the local currency. If not provided, return null.\n" +
                "- jobDescription (string): A brief description of the job duties. If not provided, return null.\n" +
                "- isPaidAnnualLeave (boolean): true if paid annual leave is provided, otherwise false.\n" +
                "- isSocialInsurance (boolean): true if social insurance is applied, otherwise false.\n" +
                "- isContractDelivery (boolean): true if the employee has been provided with a copy of the Contract, otherwise false.\n" +
                "- isSafe (boolean): true if all of the following are true: paidAnnualLeave, socialInsurance, contractDelivery. Otherwise, return false.\n\n" +
                "Example output:\n" +
                "{\n" +
                "  \"workplace\": \"메가MGC커피 화랑대역점\",\n" +
                "  \"contractStartDate\": \"2024-06-01\",\n" +
                "  \"contractEndDate\": \"2025-05-31\",\n" +
                "  \"standardWorkingStartTime\": \"10:00\",\n" +
                "  \"standardWorkingEndTime\": \"16:00\",\n" +
                "  \"workingDays\": [\"Monday\", \"Tuesday\", \"Saturday\"],\n" +
                "  \"hourlyWage\": 10200,\n" +
                "  \"jobDescription\": \"제조 및 매장 관리\",\n" +
                "  \"isPaidAnnualLeave\": true,\n" +
                "  \"isSocialInsurance\": false,\n" +
                "  \"isContractDelivery\": true,\n" +
                "  \"isSafe\": false\n" +
                "}";
    }
}
