package com.example.spwork.service.impl;

import com.example.spwork.entity.Task;
import com.example.spwork.Repository.TaskRepository;
import com.example.spwork.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> listTask() {
        return taskRepository.listAll();
    }

    public Task addTask(Task c,String level) {
        taskRepository.save(c);
        return taskRepository.refresh(c);
    }

    public void modifyTask(Task task,String level) {
        Task oldTask = taskRepository.findById(task.getId()).get();
        task.setId(oldTask.getId());
        taskRepository.save(task);
    }
    public void modifyTask_status(Task task,String level) {
        Task oldTask = taskRepository.findById(task.getId()).get();
        oldTask.setStatus(task.getStatus());
        taskRepository.save(oldTask);
    }

}
