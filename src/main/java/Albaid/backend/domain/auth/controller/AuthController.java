package Albaid.backend.domain.auth.controller;

import Albaid.backend.domain.auth.application.AuthService;
import Albaid.backend.domain.auth.dto.TokenInfo;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 클라이언트가 소셜 로그인 요청 시 사용할 엔드포인트
    @GetMapping("/login/{provider}")
    public Response<String> login(@PathVariable String provider) {
        String redirectUrl = authService.login(provider);
        return Response.success(redirectUrl);
    }

    @GetMapping("/success")
    public Response<TokenInfo> success(@RequestParam String type,
                                       @RequestParam String accessToken,
                                       @RequestParam long accessExpireDuration,
                                       @RequestParam String refreshToken,
                                       @RequestParam long refreshExpireDuration) {
        TokenInfo tokenInfo = TokenInfo.builder()
                .type(type)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessExpireDuration)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshExpireDuration)
                .build();
        return Response.success(tokenInfo);
    }

    @PostMapping("/refresh")
    public Response<TokenInfo> refresh(@RequestHeader("refreshToken") String refreshToken) {
        TokenInfo tokenInfo = authService.refresh(refreshToken);
        return Response.success(tokenInfo);
    }

    @GetMapping("/failure") // TODO : 임시로 만든 페이지 -> 나중에 삭제해야함
    public String failure() {
        return "failure";
    }
}
