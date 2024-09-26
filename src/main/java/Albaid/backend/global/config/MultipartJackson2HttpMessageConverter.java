package Albaid.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

/**
 * "Content-Type: multipart/form-data" 헤더를 지원하는 HTTP 요청 변환기
 * Swagger 사용 시 multipart 지원을 위해 생성
 * - "Content-Type 'application/octet-stream' is not supported" 에러 해결
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    protected MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, APPLICATION_OCTET_STREAM, MULTIPART_FORM_DATA);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return MULTIPART_FORM_DATA.includes(mediaType) || APPLICATION_OCTET_STREAM.includes(mediaType);
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return MULTIPART_FORM_DATA.includes(mediaType) || APPLICATION_OCTET_STREAM.includes(mediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return MULTIPART_FORM_DATA.includes(mediaType) || APPLICATION_OCTET_STREAM.includes(mediaType);
    }
}
