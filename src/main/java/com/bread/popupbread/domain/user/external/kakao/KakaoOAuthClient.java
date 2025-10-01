package com.bread.popupbread.domain.user.external.kakao;

import com.bread.popupbread.domain.user.dto.KakaoUserInfo;
import com.bread.popupbread.domain.user.dto.KakaoUserResponse;
import com.bread.popupbread.global.config.properties.KakaoProperties;
import com.bread.popupbread.global.exception.auth.AuthServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    public String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", kakaoProperties.getClientId());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("access_token")) {
                throw new AuthServiceException("카카오 응답에 access_token이 없습니다.");
            }

            return (String) body.get("access_token");
        } catch (Exception e) {
            e.printStackTrace(); // 임시 디버깅
            throw new AuthServiceException("카카오 OAuth 서버에서 access token을 받아오는 데 실패했습니다.", e);
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, KakaoUserResponse.class);

            KakaoUserResponse body = response.getBody();
            if (body == null || body.getKakao_account() == null || body.getKakao_account().getProfile() == null) {
                throw new AuthServiceException("카카오 사용자 정보가 비정상적입니다.");
            }

            return KakaoUserInfo.builder()
                    .kakaoId(body.getId())
                    .nickname(body.getKakao_account().getProfile().getNickname())
                    .email(body.getKakao_account().getEmail())
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // 임시 디버깅
            throw new AuthServiceException("카카오 유저 정보 조회 에러 발생했습니다.", e);
        }
    }
}
