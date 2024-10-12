package Albaid.backend.global.security.oauth.service;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.member.repository.MemberRepository;
import Albaid.backend.domain.resume.entity.Resume;
import Albaid.backend.domain.resume.repository.ResumeRepository;
import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.security.jwt.JwtTokenUtils;
import Albaid.backend.global.security.rft.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Date;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    private final String SUCCESS_URL;

    public OAuth2SuccessHandler(MemberRepository memberRepository, ResumeRepository resumeRepository, RefreshTokenService refreshTokenService, JwtTokenUtils jwtTokenUtils, @Value("${url.backend}") String BASE_URL) {
        this.memberRepository = memberRepository;
        this.resumeRepository = resumeRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.SUCCESS_URL = BASE_URL + "/api/auth/success";
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = oAuth2User.getName();
        Member member = memberRepository.findByProviderId(providerId);
        if (member == null) {
            throw new CustomException(NOT_FOUND_RESOURCE, "Member not found");
        }

        if (!resumeRepository.existsByMemberIdAndIsBaseResume(member.getId(), true)) {
            Resume resume = Resume.builder()
                    .member(member)
                    .title("[기본] 이력서")
                    .isBaseResume(true)
                    .build();

            resumeRepository.save(resume);
        }

        String accessToken = jwtTokenUtils.generateAccessToken(providerId);
        Date tokenExpirationDate = jwtTokenUtils.getTokenExpirationDate();
        String refreshToken = refreshTokenService.createRefreshToken(providerId);

        String redirectUrl = UriComponentsBuilder.fromUriString(SUCCESS_URL)
                .queryParam("type", "Bearer")
                .queryParam("accessToken", accessToken)
                .queryParam("accessExpireDuration", tokenExpirationDate.getTime())
                .queryParam("refreshToken", refreshToken)
                .queryParam("refreshExpireDuration", System.currentTimeMillis() + 60 * 60 * 24 * 14)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
