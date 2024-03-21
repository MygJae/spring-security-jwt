package com.example.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 토큰 사용 방시이므로 csrf 끄기, 사이트 위변조 요청 방지
        http.csrf().disable();

        // 인가(접근권한) 설정
        http.authorizeHttpRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/member/**").hasAnyRole("ADMIN", "MEMBER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/login");

        return http.build();
    }


}

