package Albaid.backend.global.config;

import Albaid.backend.domain.auth.oauth.service.OAuth2FailureHandler;
import Albaid.backend.domain.auth.oauth.service.OAuth2SuccessHandler;
import Albaid.backend.domain.auth.oauth.service.CustomOAuth2UserService;
import Albaid.backend.domain.auth.jwt.JwtTokenFilter;
import Albaid.backend.domain.auth.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenUtils jwtTokenUtils;
    private final CustomCorsConfigurationSource customCorsConfigurationSource;
    private final CustomOAuth2UserService customOAuthService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // OAuth 성공 핸들러 추가
    private final OAuth2FailureHandler oAuth2FailureHandler; // OAuth 실패 핸들러 추가

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico", "/static/**", "/public/**", "/resources/**",
                        "/login", "/logout", "/api/auth/**"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer
                        .configurationSource(customCorsConfigurationSource)
                )
                .csrf(CsrfConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                // OAuth 사용으로 인해 기본 로그인 비활성화
                .formLogin(FormLoginConfigurer::disable)
                .sessionManagement((sm) -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                // OAuth 로그인 설정
                .oauth2Login(customConfigurer -> customConfigurer
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuthService))
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenUtils), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
