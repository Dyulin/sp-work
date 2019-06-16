package com.example.spwork.controller;

import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.entity.Task;
import com.example.spwork.service.TaskService;
import com.example.spwork.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/task")
public class taskController {
    @Autowired
    private EncryptorComponent encryptorComponent;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserTaskService usertaskService;
    @GetMapping("/all")
    public Map allTask() {
        return Map.of("task", usertaskService.listAllTask());
    }
    @PostMapping("/addTask")
    public Map postTask(@RequestBody Task task, HttpServletRequest request) {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        taskService.addTask(task,level);
        return Map.of("task", taskService.listTask());
    }
    @PostMapping("/modifyTask")
    public Map modifyTask(@RequestBody Task task, HttpServletRequest request) {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        taskService.modifyTask(task,level);
        return Map.of("task", taskService.listTask());
    }
    @PostMapping("/modifyTaskStatus")
    public Map modifyTask_status(@RequestBody Task task, HttpServletRequest request) {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        taskService.modifyTask_status(task,level);
        return Map.of("task", taskService.listTask());
    }
}
