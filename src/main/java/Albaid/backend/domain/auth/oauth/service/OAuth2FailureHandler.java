package Albaid.backend.domain.auth.oauth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final String REDIRECT_URL;
    private final String ERROR_PARAM_PREFIX = "error";

    public OAuth2FailureHandler(@Value("${url.base}") String REDIRECT_URL) {
        this.REDIRECT_URL = REDIRECT_URL + "/failure";
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 리다이렉트할 주소 생성, query string에 에러 메세지 추가
        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam(ERROR_PARAM_PREFIX, exception.getLocalizedMessage())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
