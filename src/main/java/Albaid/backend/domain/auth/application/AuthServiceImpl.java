package Albaid.backend.domain.auth.application;

import Albaid.backend.domain.auth.dto.TokenInfo;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.member.repository.MemberRepository;
import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.response.ErrorCode;
import Albaid.backend.global.security.jwt.JwtTokenUtils;
import Albaid.backend.global.security.rft.entity.RefreshToken;
import Albaid.backend.global.security.rft.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Albaid.backend.global.response.ErrorCode.INVALID_TOKEN;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${url.backend}")
    private String BASE_URL;

    @Override
    public String login(String provider) {
        if (provider.equals("kakao") || provider.equals("google") || provider.equals("naver")) {
            return BASE_URL + "/oauth2/authorization/" + provider;
        }

        throw new CustomException(ErrorCode.NOT_FOUND_RESOURCE, "Provider not found");
    }

    @Override
    public TokenInfo refresh(String refreshToken) {

        RefreshToken findRefreshToken = refreshTokenService.findRefreshToken(refreshToken);

        String providerId = findRefreshToken.getProviderId();
        Member member = memberRepository.findByProviderId(providerId);
        if (member == null) {
            throw new CustomException(INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenUtils.generateAccessToken(providerId);

        return TokenInfo.builder()
                .type("Bearer")
                .accessToken(newAccessToken)
                .accessTokenExpiresIn(jwtTokenUtils.getTokenExpirationDate().getTime())
                .build();
    }
}
