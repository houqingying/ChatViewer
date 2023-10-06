package com.chatviewer.blog.filter;

import com.chatviewer.blog.service.DataService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ChatViewer
 */
@Component
public class StatisticInterceptor implements HandlerInterceptor {

    @Resource
    DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)  {
        String ip = request.getRemoteHost();
        dataService.recordIp(ip);
        return true;
    }
}
