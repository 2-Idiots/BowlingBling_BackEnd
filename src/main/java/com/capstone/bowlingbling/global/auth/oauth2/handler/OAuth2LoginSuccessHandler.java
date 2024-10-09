package com.capstone.bowlingbling.global.auth.oauth2.handler;

import com.capstone.bowlingbling.domain.member.domain.Member;
import com.capstone.bowlingbling.domain.member.repository.MemberRepository;
import com.capstone.bowlingbling.global.auth.jwt.service.JwtService;
import com.capstone.bowlingbling.global.auth.oauth2.CustomOAuth2User;
import com.capstone.bowlingbling.global.enums.Role;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            Member member = memberRepository.findByEmail(oAuth2User.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if (member.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(member.getEmail());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
                log.info("AccessToken for GUEST: {}", accessToken);
                response.sendRedirect("/sign-up");

                member.authorizeUser();
                memberRepository.save(member);
            } else {
                loginSuccess(response, member);
            }
        } catch (Exception e) {
            log.error("OAuth2 authentication success handling error", e);
            throw e;
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, Member member) throws IOException {
        JsonObject jsonObject = new JsonObject();

        String accessToken = jwtService.createAccessToken(member.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(member.getEmail(), refreshToken);

        Cookie cookie = new Cookie("refreshtoken", refreshToken);
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정
        cookie.setPath("/");
        response.addCookie(cookie);

        jsonObject.addProperty("accessToken", accessToken);
        jsonObject.addProperty("refreshToken", refreshToken);
        response.getWriter().write(jsonObject.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://bowlingbling.duckdns.org/auth/signin")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();

        // 리다이렉트 수행
        response.sendRedirect(redirectUrl);
    }
}