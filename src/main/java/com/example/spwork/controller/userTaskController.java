package com.example.spwork.controller;

import com.example.spwork.entity.*;
import com.example.spwork.service.impl.UserTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/task/comment")
public class userTaskController {
    @Autowired
    private UserTaskServiceImpl usertaskService;

    @GetMapping("/{tid}")
    public Map postTask(@PathVariable int tid) {
        return Map.of("task", usertaskService.listUserTask(tid));
    }
    @GetMapping("/crew/{cid}")
    public Map getCrew(@PathVariable int cid) {
        return Map.of("teacher", usertaskService.getTaskUser(cid));
    }
    @GetMapping("/crew")
    public Map getAllCrew() {
        return Map.of("teacher", usertaskService.listAllUser());
    }
    @PostMapping("/add")
    public Map postTask(@RequestBody comment usertask) {
        return Map.of("alert", usertaskService.addUserTask(usertask));
    }

}
