package com.bread.popupbread.unit.controller.user;

import com.bread.popupbread.domain.user.controller.UserController;
import com.bread.popupbread.domain.user.service.UserService;
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
}
