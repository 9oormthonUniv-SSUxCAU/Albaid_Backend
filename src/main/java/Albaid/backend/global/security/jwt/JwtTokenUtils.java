package Albaid.backend.global.security.jwt;

import Albaid.backend.global.response.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static Albaid.backend.global.response.ErrorCode.INVALID_TOKEN;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key key;
    private final long expirationTimeInMillis = 1000 * 60 * 60 * 2; // 2시간

    public JwtTokenUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String providerId) {

        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInMillis); // 2시간 유효

        return Jwts.builder()
                .setSubject(providerId) // providerId
                .claim("auth", "ROLE_USER")
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 만료 시간을 Date 객체로 반환
    public Date getTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + expirationTimeInMillis);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Token. " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token. " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token. " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty. " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        } catch (JwtException e) {
            log.error("JWT Token error: " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Exception error: " + e.getMessage());
            throw new CustomException(INVALID_TOKEN);
        }
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get("auth") == null) {
            throw new CustomException(INVALID_TOKEN, "권한 정보가 없는 토큰입니다.");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}