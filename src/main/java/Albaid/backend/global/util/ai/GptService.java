package Albaid.backend.global.util.ai;

import Albaid.backend.global.response.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static Albaid.backend.global.response.ErrorCode.INTERNAL_SERVER_ERROR;


@Component
@RequiredArgsConstructor
@Slf4j
public class GptService {

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_CONTENT =
            "Please extract the following information from the given Contract text. " +
                    "Ensure that the output is in strict JSON format with the appropriate field names and values. " +
                    "If any information is not provided or not applicable, return null for that field. " +
                    "All boolean values should be returned as true or false. The required fields are:\n\n" +
                    "- workplace (string): The name or address of the workplace. If not provided, return null.\n" +
                    "- contractStartDate (string): The start date of the Contract in 'YYYY-MM-DD' format. If not provided, return null.\n" +
                    "- contractEndDate (string): The end date of the Contract in 'YYYY-MM-DD' format. If not provided, return null.\n" +
                    "- standardWorkingStartTime (string): The start time of the standard working hours (e.g., '10:00'). If not provided, return null.\n" +
                    "- standardWorkingEndTime (string): The end time of the standard working hours (e.g., '16:00'). If not provided, return null.\n" +
                    "- workingDays (array): A list of days of the week the employee is expected to work (e.g., ['MO', 'TU', 'WE', 'TH', 'FR', 'SA', 'SU']). If not provided, return null.\n" +
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
                    "  \"workingDays\": [\"MO\", \"TU\", \"SA\"],\n" +
                    "  \"hourlyWage\": 10200,\n" +
                    "  \"jobDescription\": \"제조 및 매장 관리\",\n" +
                    "  \"isPaidAnnualLeave\": true,\n" +
                    "  \"isSocialInsurance\": false,\n" +
                    "  \"isContractDelivery\": true,\n" +
                    "  \"isSafe\": false\n" +
                    "}";

    public String extractContractInfoFromText(String text) throws Exception {
        HttpHeaders headers = createHeaders();
        String requestJson = createRequestJson(text);

        try {
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(gptApiUrl, HttpMethod.POST, entity, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            return jsonResponse.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + gptApiKey);
        return headers;
    }

    private String createRequestJson(String text) throws Exception {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo-0125",
                "messages", new Object[]{
                        createMessage("system", SYSTEM_CONTENT),
                        createMessage("user", text)
                }
        );
        return objectMapper.writeValueAsString(requestBody);
    }

    private Map<String, String> createMessage(String role, String content) {
        return Map.of("role", role, "content", content);
    }

}
