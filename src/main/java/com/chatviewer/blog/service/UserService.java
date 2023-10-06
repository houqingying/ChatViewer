package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.dto.UserDto;
import com.chatviewer.blog.pojo.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author ChatViewer
 */
public interface UserService extends IService<User> {

    /**
     * 注册时需要提供手机号与密码，注册成功后生成JWT Token存入Redis中
     * @param phone 手机号
     * @param code  验证码
     * @param password 密码，使用passwordEncoder加密存储至数据库
     * @return Map，包含JWT token 与 用户信息
     */
     Map<String, Object> register(String phone, String code, String password);

    /**
     * 登录时提供验证码或密码，登录成功后生成JWT Token存入Redis中
     * @param phone 手机号
     * @param code  验证码
     * @param password 密码
     * @return Map，包含JWT token 与 用户信息
     */
    Map<String, Object> login(String phone, String code, String password);

    /**
     * 生成4位数字验证码，发送至phone，存入Redis
     * @param phone 手机号
     * @return 生成的验证码
     */
    String sendPhoneCaptcha(String phone);

    /**
     * 退出登录，删除Redis中的Token
     */
    void logout();

    /**
     * 根据user更新用户信息，对于敏感字段，比如Password、TokenCount、userPhone，无法通过该接口更新
     * @param user 待更新参数
     * @return 更新后，不含敏感信息的userDto
     */
    UserDto updateUserInfo(User user);
}
