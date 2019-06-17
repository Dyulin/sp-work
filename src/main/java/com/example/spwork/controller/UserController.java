package com.example.spwork.controller;

import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.entity.User;
import com.example.spwork.service.UserService;
import com.example.spwork.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
        userService.addUser(user,level);
        return Map.of("code","200","message","添加成功");
    }

    @PostMapping("/login")
    public Map login(@RequestBody User user) {
        log.debug("账号为" + user.getAccount() + "密码为" + user.getPassword());
        Map map=userService.login(user);
        return map;
    }
    @PostMapping("/updateUser") //更新用户信息
    public Map updateUser(@RequestBody User user,HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        String nowaccount=(String)(encryptorComponent.decrypt(token).get("account"));
        log.debug("当前账户为"+nowaccount);
        log.debug(("要修改的账户为"+user.getAccount()));
        if(nowaccount.equals(user.getAccount()))
        {
            userService.update(user);
        }else{
            userService.update2(user,level);
    }
        return Map.of("code","200","message","修改成功");
    }
    @PostMapping("/delUser") //删除用户
    public Map delUser(@RequestBody User user,HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));//当前用户权限

        String level2=userService.SelectLevel(user.getAccount());//要删除的用户权限
        if(level2.equals("SUPERADMIN"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "您不能删除超级管理员");
        }else{
            userService.delUser(user,level);
            //log.debug("被删除的账号为"+user.getAccount());
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            if(jedis.exists(user.getAccount()))
            {
                jedis.del(jedis.get(user.getAccount()));
                jedis.close();
            }
        }
        return Map.of("code","200","message","删除成功");
    }
    @PostMapping("/changeAuth")  //更改权限
    public Map changeAuth(@RequestBody  User user, HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        userService.changeAuth(user, level);
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        if(jedis.exists(user.getAccount()))
        {
            jedis.del(jedis.get(user.getAccount()));
            jedis.del(user.getAccount());//更改某用户权限后强制其重新登陆。
            jedis.close();
        }
        return Map.of("code","200","message","修改权限成功");
    }
    @PostMapping("/listAll")  //所有用户信息
    public Map ListAll(@RequestBody User user,HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        List<User> userList =userService.ListAll(user,level);
        return Map.of("code","200","data",userList);
    }
    @PostMapping("/listOne")  //某个用户信息
    public Map listOne(@RequestBody User user)
    {
        User user1 =userService.Select(user.getAccount());
        return Map.of("code","200","data",user1);
    }
    @PostMapping("/updatePass") //更改密码
    public Map updatePass(@RequestBody  Map<String,String> map)
    {
        log.debug(map.get("account"));
        log.debug(map.get("oldPass"));
        log.debug(map.get("newPass"));
        String dbpass=userService.SelectPs(map.get("account"));
        String message="修改失败";
        if(passwordEncoder.matches(map.get("oldPass"),dbpass))
        {
            String newPass=passwordEncoder.encode(map.get("newPass"));
            message=userService.updatePass(newPass, map.get("account"));
        }
        return Map.of("code","200","message",message);
    }
}



















