package com.centerm.nettydecode.service.impl;

import com.centerm.nettydecode.dao.UserDao;
import com.centerm.nettydecode.pojo.User;
import com.centerm.nettydecode.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sheva
 * @date 2020/4/21 14:57
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
