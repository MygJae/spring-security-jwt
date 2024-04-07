package com.example.security1.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중");

        try {
/*
            BufferedReader reader = request.getReader();
            String input = null;
            while ((input = reader.readLine()) != null) {
                System.out.println(input);
            }
*/
            ObjectMapper objectMapper = new ObjectMapper();

            User user = objectMapper.readValue(request.getInputStream(), User.class);
            System.out.println(user.toString());

            // 1.username, password 강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // 2.로그인 시도 -> PrincipalDetailsService 호출 loadUserByUsername() 함수 실행
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3.PrincipalDetails 세션에 담기(권한관리)
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 정상: " + principalDetails.getUser().getUsername());

            return authentication;
        } catch (IOException e) {
            System.out.println("11");
            e.printStackTrace();
        }

        System.out.println("====================================");
        return null;
    }

    // 4.attemptAuthentication 정상이면 JWT 토큰 만들어서 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("인증 완료 됨");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // Hash
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())    // token 얼리어스
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10) ))  // Token 만료 시간 -> 현재시간 + 만료시간
                .withClaim("id", principalDetails.getUser().getId())    // 비공개 Claim -> 넣고싶은거 아무거나 넣으면 됨
                .withClaim("username", principalDetails.getUser().getUsername())    // 비공개 Claim
                .sign(Algorithm.HMAC512("cos"));  // HMAC512는 SECRET KEY를 필요로 함

        response.addHeader("Authentication","Bearer" + jwtToken);
    }


}

