package com.example.spwork.controller;

import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.entity.User;
import com.example.spwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private EncryptorComponent encryptorComponent;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @GetMapping("/index")
    public String index(){
        return "123";
    }
    @PostMapping("/addUser") //管理员和超管可添加用户
    public Map addUser(@RequestBody User user, HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        log.debug("用户名为"+user.getAccount()+"密码为"+user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.addUser(user,level);
        return Map.of("code","200","message","注册成功");
    }

    @PostMapping("/login")
    public Map login(@RequestBody User user) {
        log.debug("账号为" + user.getAccount() + "密码为" + user.getPassword());
        String pw = userService.SelectPs(user.getAccount());
        String level = userService.SelectLevel(user.getAccount());
        Optional.ofNullable(pw)
                .or(() -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
                })
                .ifPresent(u -> {
                    if (!passwordEncoder.matches(user.getPassword(), pw)) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
                    }  });
                    String account = user.getAccount();
                    Map map = Map.of("account", user.getAccount(),"level",level);
                    // 生成加密token
                    String token = encryptorComponent.encrypt(map);
                    // 在header创建自定义的权限
                    Jedis jedis = new Jedis("127.0.0.1", 6379);
                    jedis.set(account, token);
                    //设置key生存时间，当key过期时，它会被自动删除，时间是秒
                    jedis.expire(account, 60 * 60 * 2);
                    jedis.set(token, account);
                    jedis.expire(token, 60 * 60 * 2);
                    Long currentTime = System.currentTimeMillis();
                    jedis.set(token + account, currentTime.toString());
                    jedis.expire(token, 60 * 60 * 2);
                    log.debug(token);
                    jedis.close();
                    //response.setHeader("Authorization", token);
                    Map map2 = Map.of("code", "200", "message", "登陆成功","Authorization", token);
                    return map2;
    }
}



















