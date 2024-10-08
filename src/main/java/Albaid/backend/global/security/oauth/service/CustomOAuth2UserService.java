package Albaid.backend.global.security.oauth.service;

import Albaid.backend.global.security.UserPrincipal;
import Albaid.backend.global.security.oauth.entity.Oauth2UserInfo;
import Albaid.backend.global.security.oauth.entity.impl.GoogleUserInfo;
import Albaid.backend.global.security.oauth.entity.impl.KakaoUserInfo;
import Albaid.backend.global.security.oauth.entity.impl.NaverUserInfo;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 로그인 진행 시 키가 되는 필드값. Primary Key와 같은 의미.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 서비스를 구분하는 코드 ex) Kakao, Naver
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 소셜쪽에서 전달받은 값들을 Map 형태로 받음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Oauth2UserInfo oAuth2UserInfo = null;

        log.warn("attributes = {}", attributes.toString());

        // 카카오 || 구글 || 네이버 로그인 요청
        switch (provider) {
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                oAuth2UserInfo = new KakaoUserInfo(attributes);
            }
            case "google" -> {
                log.info("구글 로그인 요청");
                oAuth2UserInfo = new GoogleUserInfo(attributes);
            }
            case "naver" -> {
                log.info("네이버 로그인 요청");
                oAuth2UserInfo = new NaverUserInfo(attributes);
            }
        }

        Member member = getMember(oAuth2UserInfo);

        // Security context에 저장할 객체 생성
        return new UserPrincipal(member, attributes, userNameAttributeName);
    }

    private Member getMember(Oauth2UserInfo oauth2UserInfo) {
        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String name = oauth2UserInfo.getName();

        Member member = memberRepository.findByProviderId(providerId);
        if (member == null) {
            member = Member.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .name(name)
                    .build();
            memberRepository.save(member);
        }
        return member;
    }
}
