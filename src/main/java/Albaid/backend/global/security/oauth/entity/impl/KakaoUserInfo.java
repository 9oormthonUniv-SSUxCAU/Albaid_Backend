package Albaid.backend.global.security.oauth.entity.impl;

import Albaid.backend.global.security.oauth.entity.Oauth2UserInfo;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
    }
}
