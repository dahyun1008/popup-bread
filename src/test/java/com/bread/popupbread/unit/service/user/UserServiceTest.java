package com.bread.popupbread.unit.service.user;

import com.bread.popupbread.domain.user.Role;
import com.bread.popupbread.domain.user.User;
import com.bread.popupbread.domain.user.dto.KakaoUserInfo;
import com.bread.popupbread.domain.user.external.kakao.KakaoOAuthClient;
import com.bread.popupbread.domain.user.repository.UserRepository;
import com.bread.popupbread.domain.user.service.UserService;
import com.bread.popupbread.global.auth.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private KakaoOAuthClient kakaoOAuthClient;

    @Test
    void 회원이_존재하지_않으면_회원가입_진행() {
        // given
        String accessToken = "access-token";
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(123L, "nickname", "email@kakao.com");
        User savedUser = User.builder()
                .kakaoId(123L)
                .name("nickname")
                .email("email@kakao.com")
                .role(Role.USER)
                .build();

        when(kakaoOAuthClient.getAccessToken("auth-code")).thenReturn(accessToken);
        when(kakaoOAuthClient.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);
        when(userRepository.findByKakaoId(123L)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtProvider.createToken(savedUser)).thenReturn("jwt-token");

        // when
        String token = userService.loginWithKakao("auth-code");

        // then
        assertEquals("jwt-token", token);
        verify(userRepository).save(any());
        verify(jwtProvider).createToken(savedUser);
    }

    @Test
    void 회원이_이미_존재하면_저장하지_않고_JWT만_발급한다() {
        // given
        String accessToken = "access-token";
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(123L, "nickname", "email@kakao.com");
        User existingUser = User.builder()
                .kakaoId(123L)
                .name("nickname")
                .email("email@kakao.com")
                .role(Role.USER)
                .build();

        when(kakaoOAuthClient.getAccessToken("auth-code")).thenReturn(accessToken);
        when(kakaoOAuthClient.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);
        when(userRepository.findByKakaoId(123L)).thenReturn(Optional.of(existingUser));
        when(jwtProvider.createToken(existingUser)).thenReturn("jwt-token");

        // when
        String token = userService.loginWithKakao("auth-code");

        // then
        assertEquals("jwt-token", token);
        verify(userRepository).findByKakaoId(123L);
        verify(userRepository, never()).save(any()); // 새로운 유저 저장이 일어나지 않아야 통과
        verify(jwtProvider).createToken(existingUser);
    }
}
