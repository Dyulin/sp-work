package com.example.spwork.service;

import com.example.spwork.entity.Task;
import com.example.spwork.entity.User;
import com.example.spwork.entity.UserTask;

import java.util.List;
import java.util.Set;

public interface UserTaskService {
    List<UserTask> listUserTask(int tid);
    List<User> listAllUser();
    List<Task> listAllTask();
    String addUserTask(UserTask c);
    List<String> f(Set<String> a);
}
