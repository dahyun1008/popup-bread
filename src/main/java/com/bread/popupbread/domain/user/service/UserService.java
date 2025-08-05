package com.bread.popupbread.domain.user.service;

import com.bread.popupbread.domain.user.dto.KakaoUserInfo;
import com.bread.popupbread.domain.user.User;
import com.bread.popupbread.domain.user.external.kakao.KakaoOAuthClient;
import com.bread.popupbread.domain.user.Role;
import com.bread.popupbread.domain.user.repository.UserRepository;
import com.bread.popupbread.global.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final KakaoOAuthClient kakaoOAuthClient;

    @Transactional
    public String loginWithKakao(String authCode) {
        String accessToken = kakaoOAuthClient.getAccessToken(authCode);
        KakaoUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

        User user = userRepository.findByKakaoId(userInfo.getKakaoId())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(userInfo.getKakaoId())
                            .name(userInfo.getNickname())
                            .email(userInfo.getEmail())
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        return jwtProvider.createToken(user);
    }
}
