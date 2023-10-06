package com.chatviewer.blog.config;

import com.chatviewer.blog.filter.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * SpringSecurity配置类
 * @author ChatViewer
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig{


    @Resource
    JwtTokenFilter jwtAuthenticationTokenFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //禁止csrf防护
        http.csrf().disable().cors();
        //基于token, 不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/user/login","/user/register","/user/sendPhoneCaptcha").permitAll()
                .antMatchers("/category/**").permitAll()
                .antMatchers("/article/page", "/article").permitAll()
                .antMatchers(HttpMethod.GET, "/comment").permitAll()
                .antMatchers("/data/uv").permitAll()
                .antMatchers("/problem/page", "/problem").permitAll()
                //除了上面放行的请求 ，所有请求都需要鉴权
                .anyRequest().authenticated();

        // 在中间插入一个Filter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
