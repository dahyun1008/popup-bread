package com.bread.popupbread.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaoUserInfo {
    private Long kakaoId;
    private String nickname;
    private String email;
}