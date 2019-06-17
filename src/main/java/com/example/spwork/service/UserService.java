package com.example.spwork.service;

import com.example.spwork.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map login(User u);
    void addUser(User u, String level);
    User findByAccount(String account);
    User Select(String account);
    String SelectPs(String account);
    String SelectLevel(String account);
    void update(User user);
    void update2(User user,String level);
    void changeAuth(User user,String level);
    String updatePass(String newpass,String account);
    List ListAll(User user, String level);
    void delUser(User user,String level);
}
