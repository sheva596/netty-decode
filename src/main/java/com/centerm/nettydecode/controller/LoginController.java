package com.centerm.nettydecode.controller;

import com.centerm.nettydecode.aop.log.Log;
import com.centerm.nettydecode.pojo.Result;
import com.centerm.nettydecode.pojo.User;
import com.centerm.nettydecode.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

/**
 * @author Sheva
 * @date 2020/4/21 14:26
 * @description
 */

@Controller
@Api(value = "api", tags = {"登录测试"})
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;


    @CrossOrigin
    @ApiOperation("用户登陆")
    @Log("用户登陆")
    @PostMapping("api/login")
    @ResponseBody
    public Result login(@RequestBody User reqUser){
        String username = reqUser.getUsername();
        username = HtmlUtils.htmlEscape(username);
        User user = userService.findByUsername(username);
        log.info("数据库查询结果： " + user.toString());
        if (!Objects.equals(user.getUsername(), username) || !Objects.equals(user.getPassword(), reqUser.getPassword())){
            log.info("账号密码错误...");
            return new Result(400);
        }
        return new Result(200);
    }
}
