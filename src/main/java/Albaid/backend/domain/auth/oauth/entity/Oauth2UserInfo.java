package Albaid.backend.domain.auth.oauth.entity;

public interface Oauth2UserInfo {
    String getProviderId();

    String getProvider();

    String getName();
}
