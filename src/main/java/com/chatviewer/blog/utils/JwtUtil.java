package com.chatviewer.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author ChatViewer
 **/
public class JwtUtil {

    /**
     * JWT的Secret Key，需要用其验证JWT的有效性
     */
    private static final String SECRET = "chatviewer";
    /**
     * JWT签发者
     */
    private static final String ISS = "ChatViewer";
    /**
     * JWT有效时间
     */
    private static final long EXPIRATION = 2 * 3600L;
    private static final String ID_KEY = "id";


    /**
     * 对userId加密，生成token
     * @param userId 用户Id
     * @return JWT token
     */
    public static String createToken(Long userId) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .claim(ID_KEY, userId.toString())
                // JWT的签发者
                .setIssuer(ISS)
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();
    }


    /**
     * 调用getTokenBody方法对token解密，并得到存在其中的userId
     * @param token 待解密的JWT
     * @return userId
     */
    public static Long getUserId(String token) {
        // 从token中获取用户Id
        return Long.parseLong((String) getTokenBody(token).get(ID_KEY));
    }


    /**
     * @param token 待解密的JWT
     * @return token是否过期
     */
    public static boolean isExpiration(String token) {
        // 是否已过期
        try {
            return getTokenBody(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * @param token 待解密的JWT
     * @return 解密后的token Claims
     */
    private static Claims getTokenBody(String token) {
        // 是否能用SECRET解密，并返回其PayLoad，Claims继承了HashMap类，可以看作一个Map
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

}