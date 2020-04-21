package com.centerm.nettydecode.service;

import com.centerm.nettydecode.pojo.User;

/**
 * @author Sheva
 * @date 2020/4/21 14:57
 * @description
 */
public interface UserService {

    /**
     * 通过用户名寻找用户
     * @param username  用户名
     * @return user
     */
    User findByUsername(String username);
}
