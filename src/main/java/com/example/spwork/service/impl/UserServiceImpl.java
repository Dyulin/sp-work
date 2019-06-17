package com.example.spwork.service.impl;


import com.example.spwork.Repository.UserRepository;
import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.component.MyAuthority;
import com.example.spwork.entity.User;
import com.example.spwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EncryptorComponent encryptorComponent;
    public Map login(User user)
    {
        log.debug("账号为" + user.getAccount() + "密码为" + user.getPassword());
        User user1=userRepository.find(user.getAccount());
        Optional.ofNullable(user1)
                .or(() -> {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名不存在");
                })
                .ifPresent(u->{
        String pw = u.getPassword();
        if (!passwordEncoder.matches(user.getPassword(), pw))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密码输入错误");
        }});
        String level = userRepository.findLevel(user.getAccount());
        String account = user.getAccount();
        Map map = Map.of("account", account,"level",level);
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
        Map map2 = Map.of("code", "200", "message",
                "登陆成功","Authorization", token,"role",level,"account",account);
        return map2;
    }
    @MyAuthority(value= MyAuthority.MyAuthorityType.ADMIN)
    public void addUser(User u,String level) {
        try{
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            userRepository.save(u);
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名已存在");
        }
    }
    public String SelectPs(String account)
    {
        return userRepository.find(account).getPassword();
    }
    public String SelectLevel(String account)
    {
        return  userRepository.findLevel(account);
    }
    public User Select(String account)
    {
        return  userRepository.find(account);
    }
    public void update(User user) {
        try{
            //String newPass=passwordEncoder.encode(user.getPassword());
            userRepository.updateUser(user.getIntro(),user.getName(), user.getPhone()
                    ,user.getPosition(),user.getAccount());
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "修改信息失败");
        }
    }
    @MyAuthority(value= MyAuthority.MyAuthorityType.ADMIN)
    public void update2(User user,String level) {
        try{
            userRepository.updateUser(user.getIntro(),user.getName(), user.getPhone()
                    ,user.getPosition(),user.getAccount());
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "修改信息失败");
        }
    }
    @MyAuthority(value= MyAuthority.MyAuthorityType.SUPERADMIN)
    public void changeAuth(User user,String level) {

        try{
            userRepository.updateAuth(user.getLevel(),user.getAccount());
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "修改权限失败");
        }
    }

    @MyAuthority(value = MyAuthority.MyAuthorityType.ADMIN)
    public List ListAll(User user,String level)
    {
        return userRepository.findAllByOrderByIdDesc();
    }
    public String updatePass(String newpass,String account)
    {
        try{
            userRepository.updatePass(newpass, account);
        }catch(Exception e)
        {
            return "修改密码失败";
        }
        return "success";
    }
    @MyAuthority(MyAuthority.MyAuthorityType.SUPERADMIN)
    public void delUser(User user,String level)
    {
        try{
            userRepository.deleteByAccount(user.getAccount());
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "删除失败");
        }
    }
    @Override
    public User findByAccount(String account)
    {
        return userRepository.find(account);
    }
}
