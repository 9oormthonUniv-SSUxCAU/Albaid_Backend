package Albaid.backend.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenInfo(String type, String accessToken, String refreshToken, long accessTokenExpiresIn,
                        long refreshTokenExpiresIn) {
}
