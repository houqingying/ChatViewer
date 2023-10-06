package com.chatviewer.blog.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author ChatViewer
 */
public class ContextHolderUtil {

    /**
     * @return 获取SecurityContextHolder中的id
     */
    public static Long getUserId() {
        try {
            // 获取SecurityContextHolder中的用户id
            UsernamePasswordAuthenticationToken authenticationToken =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return (Long) authenticationToken.getPrincipal();
        }
        catch (Exception e) {
            return null;
        }
    }

}
