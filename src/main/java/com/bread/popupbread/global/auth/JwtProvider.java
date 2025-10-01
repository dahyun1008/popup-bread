package com.bread.popupbread.global.auth;

import com.bread.popupbread.domain.user.User;
import com.bread.popupbread.global.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final JwtProperties jwtProperties;
    private Key key;

    @jakarta.annotation.PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            // 서명 불일치 (가장 흔한 원인)
            System.err.println("Invalid JWT signature: " + ex.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            // 구조적으로 잘못된 JWT
            System.err.println("Invalid JWT token: " + ex.getMessage());
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // 만료된 JWT 토큰 (이 경우는 아닐 가능성이 높음)
            System.err.println("Expired JWT token: " + ex.getMessage());
            return false;
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            // 지원되지 않는 JWT 토큰
            System.err.println("Unsupported JWT token: " + ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            // JWT 클레임 문자열이 비어있음
            System.err.println("JWT claims string is empty: " + ex.getMessage());
            return false;
        } catch (Exception ex) { // 위에서 잡히지 않은 모든 예외
            System.err.println("JWT validation failed: " + ex.getMessage());
            ex.printStackTrace(); // 스택 트레이스 출력하여 상세 정보 확인
            return false;
        }
    }
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
