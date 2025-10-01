package com.bread.popupbread.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 카카오_로그인_성공시_JWT_토근_반환됨() throws Exception {
        // given
        String kakaoAccessToken = "dummy_kakao_token";

        // when & then
        mockMvc.perform(post("/api/v1/auth/kakao")
                .header("Authorization", "Bearer " + kakaoAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }
}
