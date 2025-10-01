package com.bread.popupbread.domain.user;

import com.bread.popupbread.domain.user.external.kakao.KakaoOAuthClient;
import com.bread.popupbread.global.exception.auth.AuthServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("local")
public class KakaoOAuthClientTest {

    @Autowired
    private KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void 인가코드가_잘못되어_카카오에서_에러반환시_예외() {
        // given
        String code = "invalid_code";
        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThrows(AuthServiceException.class, () -> kakaoOAuthClient.getAccessToken(code));
    }

    @Test
    void 액세스_토큰_API_타임아웃시_예외() {
        // given
        String code = "valid_code";

        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(request -> {
                    throw new SocketTimeoutException();
                });

        // when & then
        assertThrows(AuthServiceException.class, () -> kakaoOAuthClient.getAccessToken(code));
    }

    @Test
    void 유저_정보_API_타임아웃시_예외() {
        // given
        String token = "valid_token";

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andRespond(request -> {
                    throw new SocketTimeoutException();
                });

        // when & then
        assertThrows(AuthServiceException.class, () -> kakaoOAuthClient.getUserInfo(token));
    }

    @Test
    void 유효하지_않은_유저정보_응답시_예외() {
        // given
        String token = "valid_token";

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // when & then
        assertThrows(AuthServiceException.class, () -> kakaoOAuthClient.getUserInfo(token));
    }
}
