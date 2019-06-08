package com.example.spwork.service;


import com.example.spwork.Repository.UserRepository;
import com.example.spwork.component.MyAuthority;
import com.example.spwork.entity.AccountLevel;
import com.example.spwork.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @MyAuthority(value= MyAuthority.MyAuthorityType.ADMIN)
    public void addUser(User u,String level) {
        try{
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
    public List Listall(User user,String level)
    {
        return userRepository.findAllByOrderByIdDesc();
    }
    public void updatePass(String newpass,String account)
    {
        try{
            userRepository.updatePass(newpass, account);
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "修改密码失败");
        }
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
}
