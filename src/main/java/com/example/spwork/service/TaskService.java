package com.example.spwork.service;

import com.example.spwork.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> listTask();
    Task addTask(Task c,String level);
    void modifyTask(Task task,String level);
    void modifyTask_status(Task task,String level);

}
