package Albaid.backend.global.security.rft.service;


import Albaid.backend.global.security.rft.entity.RefreshToken;
import Albaid.backend.global.security.rft.repository.RefreshTokenRepository;
import Albaid.backend.global.response.CustomException;
import Albaid.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String providerId) {
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken tokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .providerId(providerId)
                .build();
        refreshTokenRepository.save(tokenEntity);
        return refreshToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken).
                orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
    }
}
