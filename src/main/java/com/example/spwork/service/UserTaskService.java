package com.example.spwork.service;

import com.example.spwork.entity.*;

import java.util.List;
import java.util.Set;

public interface UserTaskService {
    List<usertaskk> listUserTask(int tid);
    List<User> listAllUser();
    List<Task> listAllTask();
    String addUserTask(comment c);
    List<String> f(Set<String> a);
}
