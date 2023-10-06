package com.chatviewer.blog.filter;

import cn.hutool.core.util.StrUtil;
import com.chatviewer.blog.utils.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import static com.chatviewer.blog.constant.RedisConstant.USER_TOKEN_KEY;

/**
 * JWT过滤器：
 * 对每个请求，检查其是否携带token，
 * 如果携带token，解析token并将userId写入SecurityContextHolder中
 * @author ChatViewer
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        // 获取token
        String token = request.getHeader("token");
        if (!StrUtil.isNotBlank(token)) {
            // 没有token直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 有token，获取userId
        Long userId;
        try {
            userId = JwtUtil.getUserId(token);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token illegal");
        }

        // 检查是否与redis中的token相同（是否退出登录）
        if (!token.equals(stringRedisTemplate.opsForValue().get(USER_TOKEN_KEY + userId))) {
            throw new RuntimeException("token illegal");
        }

        // 将userId信息存入SecurityContextHolder
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }
}
