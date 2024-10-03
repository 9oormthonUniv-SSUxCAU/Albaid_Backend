package Albaid.backend.domain.auth.controller;

import Albaid.backend.domain.auth.dto.TokenInfo;
import Albaid.backend.domain.auth.jwt.JwtTokenUtils;
import Albaid.backend.domain.auth.rft.entity.RefreshToken;
import Albaid.backend.domain.auth.rft.service.RefreshTokenService;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.member.repository.MemberRepository;
import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static Albaid.backend.global.response.ErrorCode.INVALID_TOKEN;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${url.base}")
    private String BASE_URL;

    // 클라이언트가 소셜 로그인 요청 시 사용할 엔드포인트
    @GetMapping("/login/{provider}")
    public Response<String> login(@PathVariable String provider) {
        String redirectUrl = BASE_URL + "/oauth2/authorization/" + provider;
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
        RefreshToken findRefreshToken = refreshTokenService.findRefreshToken(refreshToken);

        String providerId = findRefreshToken.getProviderId();
        Member member = memberRepository.findByProviderId(providerId);
        if (member == null) {
            throw new CustomException(INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenUtils.generateAccessToken(providerId);
        TokenInfo tokenInfo = TokenInfo.builder()
                .type("Bearer")
                .accessToken(newAccessToken)
                .accessTokenExpiresIn(jwtTokenUtils.getTokenExpirationDate().getTime())
                .build();

        return Response.success(tokenInfo);
    }

    @GetMapping("/failure") // TODO : 임시로 만든 페이지 -> 나중에 삭제해야함
    public String failure() {
        return "failure";
    }
}
