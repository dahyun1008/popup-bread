package com.bread.popupbread.unit.controller.user;

import com.bread.popupbread.domain.user.controller.UserController;
import com.bread.popupbread.domain.user.service.UserService;
import com.bread.popupbread.global.exception.auth.AuthServiceException;
import com.bread.popupbread.global.exception.auth.IllegalCodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void 인가코드로_로그인_성공시_JWT_쿠키_설정하고_리다이렉트() throws Exception {
        // given
        String authCode = "dummy-auth-code";
        String expectedJwt = "jwt.token.value";
        when(userService.loginWithKakao(authCode)).thenReturn(expectedJwt);

        // when & then
        mockMvc.perform(get("/api/auth/kakao/callback")
                        .param("code", authCode))
                .andExpect(status().isFound()) // 302
                .andExpect(redirectedUrl("/popups"))
                .andExpect(cookie().maxAge("access-token", 3600))
                .andExpect(cookie().path("access-token", "/"))
                .andExpect(cookie().value("access-token", expectedJwt))
                .andExpect(cookie().httpOnly("access-token", true));
    }

    @Test
    void 인가코드_없이_콜백요청시_에러쿼리포함_로그인페이지_리다이렉트() throws Exception {
        mockMvc.perform(get("/api/auth/kakao/callback"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error=missing_code"));
    }

    @Test
    void 잘못된_인가코드일_경우_에러쿼리포함_로그인페이지_리다이렉트() throws Exception {
        String invalidCode = "invalid-code";
        when(userService.loginWithKakao(invalidCode)).thenThrow(new IllegalCodeException("잘못된 인가코드"));

        mockMvc.perform(get("/api/auth/kakao/callback")
                .param("code", invalidCode))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error=illegal_code"));
    }

    @Test
    void 서비스_내부_예외발생시_에러쿼리포함_로그인페이지_리다이렉트() throws Exception {
        String authCode = "valid-but-fails-in-service";
        when(userService.loginWithKakao(authCode)).thenThrow(new AuthServiceException("내부 에러"));

        mockMvc.perform(get("/api/auth/kakao/callback")
                .param("code", authCode))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error=auth_service"));
    }
}
