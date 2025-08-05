package com.bread.popupbread.domain.user.controller;

import com.bread.popupbread.domain.user.service.UserService;
import com.bread.popupbread.global.exception.auth.MissingCodeException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(
            @RequestParam(value = "code", required = false) String code,
            HttpServletResponse response
    ) {
        if (code == null || code.isBlank()) {
            throw new MissingCodeException("인가코드 누락");
        }
        String jwt = userService.loginWithKakao(code);

        ResponseCookie cookie = ResponseCookie.from("access-token", jwt)
                .httpOnly(true)
                .maxAge(3600)
                .sameSite("Lax")
                .path("/")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/popups"))
                .build();
    }
}
