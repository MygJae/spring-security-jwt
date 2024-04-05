package com.example.security1.config;

import com.example.security1.config.oauth.PrincipalOauth2UserService;
import com.example.security1.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured, PreAuthorize 어노테이션 활성화
@Slf4j
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    private final CorsConfig corsConfig;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
//        UsernamePasswordAuthenticationFilter  BasicAuthenticationFilter

        // 토큰 사용 방시이므로 csrf 끄기, 사이트 위변조 요청 방지
        http.csrf().disable();

        // Clickjacking 공격으로부터 보호하기 위함
        http.headers().frameOptions().sameOrigin();

        // 세션
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsConfig.corsFilter())
                .httpBasic().disable();

        // 인가(접근권한) 설정
        http.authorizeHttpRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login().loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
        ;

        return http.build();
    }
//순환참조 발생하여 따로 만들어 빈(컴포넌트)등록
/*
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }
*/

}