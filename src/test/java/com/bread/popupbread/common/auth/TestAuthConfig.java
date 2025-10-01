package com.bread.popupbread.common.auth;

import com.bread.popupbread.domain.user.User;
import com.bread.popupbread.domain.user.repository.UserRepository;
import com.bread.popupbread.global.auth.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.function.Supplier;

@TestConfiguration
public class TestAuthConfig {

    @Bean
    public Supplier<String> testJwtProvider(
            UserRepository userRepository,
            JwtProvider jwtProvider,
            @Value("${test.user.kakaoId}") Long kakaoId
    ) {
        return () -> {
            User user = userRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new IllegalStateException("테스트용 유저(kakaoId=" + kakaoId + ")가 DB에 없음"));
            return jwtProvider.createToken(user);
        };
    }
}
