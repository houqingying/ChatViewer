package com.chatviewer.blog.dto;

import com.chatviewer.blog.pojo.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 实现UserDetails接口的DTO
 * UserDetails为SpringSecurity UserDetailService接口loadUserByUsername方法的返回对象
 * 该方法在UserServiceImpl中被重写
 * 核心：重写getUsername和getPassword，即登录的凭证与密码，在这里分别对应手机号与密码
 * TODO: 目前只是账号校验，可以考虑加入权限控制，比如一些user是管理员
 * @author ChatViewer
 */
@Data
public class UserDtoForLogin implements UserDetails {

    private User user;

    public UserDtoForLogin(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserPhone();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
