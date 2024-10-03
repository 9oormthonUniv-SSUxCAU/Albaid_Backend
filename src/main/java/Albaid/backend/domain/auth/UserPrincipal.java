package Albaid.backend.domain.auth;

import Albaid.backend.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User {

    private Member member;
    private String nameAttributeKey;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Member member, Map<String, Object> attributes, String nameAttributeKey) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    /**
     * OAuth2User method implements
     */
    @Override
    public String getName() {
        return member.getProviderId();
    }
}
