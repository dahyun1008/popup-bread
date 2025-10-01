package com.bread.popupbread.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider) {this.jwtProvider = jwtProvider;}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        String path = req.getRequestURI();

        return path.startsWith("/login")
                || path.startsWith("/images")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/api/auth/kakao/callback")
                || path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        System.out.println("--- JwtAuthFilter: doFilterInternal HIT! Request URI: " + req.getRequestURI() + " ---");

        String token = resolveFromCookie(req, "access-token");

        if (token != null && jwtProvider.validateToken(token)) {
            Long userId = jwtProvider.getUserId(token);
            String role = jwtProvider.getRole(token);

            List<GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            UserDetails userDetails = new UserDetails() {
                @Override
                public java.lang.String getUsername() {
                    return String.valueOf(userId);
                }
                @Override
                public java.lang.String getPassword() {
                    return "";
                }
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return authorities;
                }
                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }
                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }
                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }
                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }

    private String resolveFromCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
