package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.pojo.User;
import com.chatviewer.blog.service.UserService;
import com.chatviewer.blog.utils.FormatChecker;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户Controller类，用于分发用户相关http请求并返回响应
 * CrossOrigin注解用于解决前后端联调时的跨域问题
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    private static final Integer MIN_PSW_LEN = 6;


    @PostMapping("/register")
    public Result<Object> register(@RequestBody Map<String, Object> map) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("captcha");
        String password = (String) map.get("password");

        // 校验手机号与验证码格式，以及是否为空
        if (!FormatChecker.isPhone(phone)) {
            return Result.fail(400, "手机号格式错误");
        }
        if (!FormatChecker.isCode(code)) {
            return Result.fail(400, "验证码格式错误");
        }
        if (password == null || password.length() < MIN_PSW_LEN) {
            return Result.fail(400, "密码为空或密码长度过短");
        }

        // 调用userService
        Map<String, Object> tokenAndUserMap = userService.register(phone, code, password);
        if (tokenAndUserMap == null) {
            return Result.fail(400, "验证码输入错误，或用户已注册");
        }
        return Result.success(tokenAndUserMap);
    }


    @PostMapping("/login")
    public Result<Object> login(@RequestBody Map<String, Object> map) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("captcha");
        String password = (String) map.get("password");

        // 校验手机号格式，以及是否为空
        if (!FormatChecker.isPhone(phone)) {
            return Result.fail(400, "手机号格式错误");
        }

        // 验证码非空时，校验验证码格式
        if (code != null && !FormatChecker.isCode(code)) {
            // 验证码格式错误
            return Result.fail(400, "选择使用验证码登录，但验证码格式错误");
        }

        // 返回用户信息map，key为"user"和"token"
        Map<String, Object> tokenAndUserMap = userService.login(phone, code, password);
        if (tokenAndUserMap == null) {
            // 返回null说明登录失败啦
            return Result.fail(400, "登录失败");
        }
        return Result.success(tokenAndUserMap);
    }


    @GetMapping("/logout")
    public Result<Object> logout() {
        userService.logout();
        return Result.success("用户退出登录");
    }


    @GetMapping("/sendPhoneCaptcha")
    public Result<Object> sendPhoneCaptcha(String phone) {
        // 校验手机号是否为空及其格式
        if (!FormatChecker.isPhone(phone)) {
            log.info("The phone input is {}, which has the wrong format.", phone);
            return Result.fail(400, "手机号格式错误");
        }
        // userService发送验证码、存入Redis，并返回生成的验证码给controller
        String code = userService.sendPhoneCaptcha(phone);
        return Result.success(code);
    }


    @GetMapping("/userInfo")
    public Result<Object> queryUserInfo() {
        // 获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        // 只能查询自己的用户信息吼~
        Long userId = (Long) authenticationToken.getPrincipal();
        return Result.success(userService.getById(userId));
    }


    @PutMapping("/userInfo")
    public Result<Object> updateUserInfo(@RequestBody User user) {
        // 获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authenticationToken.getPrincipal();

        if (!userId.equals(user.getUserId())) {
            return Result.fail("只能更改自己的用户信息哦！");
        }

        return Result.success(userService.updateUserInfo(user));
    }

}
