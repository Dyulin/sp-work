package com.example.spwork.controller;

import com.example.spwork.component.AuthorityAspect;
import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.entity.User;
import com.example.spwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.Encoder;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.addUser(user,level);
        return Map.of("code","200","message","添加成功");
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
    @GetMapping("/listAll")  //所有用户信息
    public Map ListAll(@RequestBody User user,HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        List<User> userList =userService.Listall(user,level);
        return Map.of("code","200","data",userList);
    }
    @GetMapping("/listOne")  //某个用户信息
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
        if(passwordEncoder.matches(map.get("oldPass"),dbpass))
        {
            String newPass=passwordEncoder.encode(map.get("newPass"));
            userService.updatePass(newPass, map.get("account"));
            return Map.of("code","200","message","修改密码成功");
        }else
         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "初始密码错误");

    }
}



















