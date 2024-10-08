package Albaid.backend.domain.auth.application;

import Albaid.backend.domain.auth.application.dto.TokenInfo;

public interface AuthService {
    String login(String provider);

    TokenInfo refresh(String refreshToken);
}
