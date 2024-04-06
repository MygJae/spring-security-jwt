package com.example.security1.config.jwt;

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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중");

        // 1.username, password
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
            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // loadUserByUsername() 함수 실행
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // authentication 객체가 session에 저장
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 정상: " + principalDetails.getUser().getUsername());

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("====================================");

        // 2.로그인 시도 -> PrincipalDetailsService 호출 loadUserByUsername() 함수 실행

        // 3.PrincipalDetails 세션에 담기(권한관리)

        // 4.JWT 토큰 만들어서 응답

        return null;
    }

    //attemptAuthentication 정상이면 -> JWT 토큰 만들어서 줌
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("인증 완료 됨");
        super.successfulAuthentication(request, response, chain, authResult);
    }


}

