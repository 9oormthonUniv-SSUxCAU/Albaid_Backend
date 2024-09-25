package Albaid.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    BAD_REQUEST_RESOURCE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "찾을 수 없는 리소스입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
