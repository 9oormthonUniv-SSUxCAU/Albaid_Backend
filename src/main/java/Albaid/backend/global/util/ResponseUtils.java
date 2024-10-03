package Albaid.backend.global.util;

import Albaid.backend.global.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseUtils() {
    }

    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        Response<Void> body = Response.error(message);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(body);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
