package com.chatviewer.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.UserDto;
import com.chatviewer.blog.dto.UserDtoForLogin;
import com.chatviewer.blog.mapper.UserMapper;
import com.chatviewer.blog.pojo.User;
import com.chatviewer.blog.service.UserService;
import com.chatviewer.blog.utils.FormatChecker;
import com.chatviewer.blog.utils.JwtUtil;
import com.chatviewer.blog.utils.AliyunSendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.chatviewer.blog.constant.RedisConstant.*;
import static com.chatviewer.blog.constant.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * UserService的实现类
 * 继承了Security中的UserDetailService
 * @author ChatViewer
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    AliyunSendSms sendSms;


    /**
     * SpringSecurity需要的一些类，加密存储，对password进行编码再存入数据库，
     * 登录时先加密再进行比较
     * 将基于此创建AuthenticationManager类
     */
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    /**
     * 设置好PasswordEncoder和UserDetailsService，以简化对密码的验证过程（无需自己写Encode、Decode
     */
    private final AuthenticationManager authenticationManager = authenticationManager();
    private AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // 添加passwordEncoder
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        // 添加UserDetailsService
        daoAuthenticationProvider.setUserDetailsService(this);
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * 实现Security中的UserDetailService接口，根据提供的参数，在数据库中查找对应的User对象及权限信息
     * @param phone 用户手机号
     * @return UserDetails对象，包含用户信息及权限信息
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        if (!FormatChecker.isPhone(phone)) {
            throw new UsernameNotFoundException("手机号格式错误");
        }

        User user = getByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("手机号不存在");
        }

        return new UserDtoForLogin(user);
    }


    @Override
    public Map<String, Object> register(String phone, String code, String password) {

        // 1、校验验证码是否正确，与Redis中存储的验证码是否相同
        String codeStored = stringRedisTemplate.opsForValue().get(USER_CODE_KEY + phone);
        if (!code.equals(codeStored)) {
            //不相同的话直接返回null
            return null;
        }

        User user = getByPhone(phone);

        // 2、如果不存在，创建用户，返回token
        if (user == null) {
            // 创建根据phone和password创建user
            user = insertByPhoneAndPsw(phone, password);

            // 生成用户的Jwt token
            String token = JwtUtil.createToken(user.getUserId());
            // Redis中存入以userId为key，存入token，表示该用户已登录
            saveUserJwt2Redis(user, token);

            // 构造返回结果
            Map<String, Object> map = new HashMap<>(4);
            map.put("token", token);
            map.put("user", user);

            return map;
        }

        // 3、否则用户已经注册过，不能重复注册
        return null;
    }


    @Override
    public Map<String, Object> login(String phone, String code, String password) {

        // 进行登录校验，code为null时, 使用password登录
        User user = code == null ? loginByPassword(phone, password) : loginByCode(phone, code);
        // 如果登录成功，user将为数据库中查找到的User对象，否则为null
        if (user == null) {
            return null;
        }

        // 生成用户的TOKEN并返回，Redis中存入该用户的信息与token
        String token = JwtUtil.createToken(user.getUserId());
        saveUserJwt2Redis(user, token);

        // 构造返回结果
        Map<String, Object> map = new HashMap<>(4);
        map.put("token", token);
        map.put("user", user);

        return map;
    }


    /**
     * 将token存入Redis
     * @param user User对象
     * @param token 存有userId的JWT token
     */
    public void saveUserJwt2Redis(User user, String token) {
        stringRedisTemplate.opsForValue().set(USER_TOKEN_KEY + user.getUserId(), token, USER_TOKEN_TTL, TimeUnit.MINUTES);
    }


    /**
     * 使用密码登录，在login函数中被调用
     * @param phone 手机号
     * @param password 密码
     * @return 密码码正确，返回user，否则返回null
     */
    public User loginByPassword(String phone, String password) {
        // 构造UsernamePasswordAuthenticationToken类对象，作为authenticate的参数
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phone, password);
        // 直接调用.authenticate，省略了对password先编码再比较的过程
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication == null) {
            // 登录失败
            log.error("choose to login by password, but failed.");
            return null;
        }
        return getByPhone(phone);
    }


    /**
     * 使用验证码登录，在login函数中被调用
     * @param phone 手机号
     * @param code 验证码
     * @return 验证码正确，返回user，否则返回null
     */
    public User loginByCode(String phone, String code) {
        // 校验验证码是否正确，与Redis中存储的验证码是否相同
        String codeStored = stringRedisTemplate.opsForValue().get(USER_CODE_KEY + phone);
        // 验证码正确，从数据库中读取user信息并返回
        if (code.equals(codeStored)) {
            return getByPhone(phone);
        }
        return null;
    }


    @Override
    public String sendPhoneCaptcha(String phone) {
        // 生成并发送验证码
        String code = sendSms.sendMessage(phone);
        log.info("The code is {}.", code);

        // code存入Redis
        stringRedisTemplate.opsForValue().set(USER_CODE_KEY + phone, code, USER_CODE_TTL, TimeUnit.MINUTES);
        return code;
    }


    public void saveUser2Redis(User user) {
        HashMap<String, Object> userMap = new HashMap<>(10);
        // 拷贝时转为字符串
        BeanUtil.beanToMap(user, userMap,  CopyOptions.create().setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ?fieldValue.toString() : null));
        stringRedisTemplate.opsForHash().putAll(USER_INFO_KEY + user.getUserId().toString(), userMap);
        stringRedisTemplate.expire( USER_INFO_KEY + user.getUserId().toString(), USER_INFO_TTL, TimeUnit.MINUTES);
    }


    /**
     * @param phone 手机号
     * @return 根据phone关键字查找到的User
     */
    public User getByPhone(String phone) {
        // 查找用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserPhone, phone);
        return this.getOne(queryWrapper);
    }


    /**
     * 根据phone和password创建用户，
     * userName随机生成，userAvatar设置默认值、userTokenCount设置为10
     * @param phone 手机号，全局唯一
     * @param password 密码，加密存储至数据库
     * @return 创建的User对象
     */
    public User insertByPhoneAndPsw(String phone, String password) {
        User user = new User();
        user.setUserPhone(phone);
        // 密码加密存储
        user.setUserPassword(passwordEncoder.encode(password));
        // 随机生成用户名，使用Hutool的工具类
        user.setUserName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 设置默认头像
        user.setUserAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        // 设置初始token数量
        user.setUserTokenCount(10);
        // ID在User类中通过@Table设置，使用雪花算法，调用save后User对象将自动填充ID
        this.save(user);
        return user;
    }


    @Override
    public void logout() {
        // 获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authenticationToken.getPrincipal();
        // 删除缓存中的Token
        stringRedisTemplate.delete(USER_TOKEN_KEY + userId);
    }

    @Override
    public UserDto updateUserInfo(User user) {
        // 用户的敏感信息不允许随意更新
        user.setUserPassword(null);
        user.setUserTokenCount(null);
        user.setUserPhone(null);
        // 更新信息
        updateById(user);
        User updatedUser = getById(user.getUserId());
        return BeanUtil.copyProperties(updatedUser, UserDto.class);
    }

}
