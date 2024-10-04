package Albaid.backend.domain.auth.jwt;

import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.response.ErrorCode;
import Albaid.backend.global.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_ERROR_MESSAGE = "유효한 인증 토큰이 필요합니다.";

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            processTokenAuthentication(request);
        } catch (CustomException e) {
            log.error("Invalid Token. " + e.getMessage());
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, TOKEN_ERROR_MESSAGE);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void processTokenAuthentication(HttpServletRequest request) {
        String token = getTokenFromRequest(request);

        // 토큰이 있는 경우 검증 및 인증 처리
        if (StringUtils.hasText(token)) {
            if (jwtTokenUtils.validateToken(token)) {
                try {
                    Authentication authentication = jwtTokenUtils.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return;
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN);
                }
            }
        }

        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
