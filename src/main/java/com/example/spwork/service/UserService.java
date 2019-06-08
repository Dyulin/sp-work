package com.example.spwork.service;


import com.example.spwork.Repository.UserRepository;
import com.example.spwork.component.MyAuthority;
import com.example.spwork.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    @MyAuthority(value = MyAuthority.MyAuthorityType.SUPERADMIN)
    public void addAdmin(User u,String level) {
        try{
            userRepository.save(u);
        }catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名已存在");
        }
    }
    public String SelectPs(String account)
    {
        return userRepository.find(account);
    }
    public String SelectLevel(String account)
    {
        return userRepository.findLevel(account);
    }
}
