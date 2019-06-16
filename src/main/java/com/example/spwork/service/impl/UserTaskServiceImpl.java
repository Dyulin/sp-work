package com.example.spwork.service.impl;

import com.example.spwork.entity.Task;
import com.example.spwork.entity.User;
import com.example.spwork.entity.UserTask;
import com.example.spwork.Repository.TaskRepository;
import com.example.spwork.Repository.UserRepository;
import com.example.spwork.Repository.UserTaskRepository;
import com.example.spwork.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
public class UserTaskServiceImpl implements UserTaskService {
    @Autowired
    private UserTaskRepository usertaskRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public List<UserTask> listUserTask(int tid) {
        return usertaskRepository.list(tid);
    }
    public List<User> listAllUser() {
        return userRepository.listAll();
    }
    public List<Task> listAllTask(){return taskRepository.listAll();}
    public String addUserTask(UserTask c) {
        int a = usertaskRepository.judge(c.getTask().getId(), c.getUser().getId());
        usertaskRepository.save(c);
        if(a>0)return "已进行过回复，建议不要重复提交";
        usertaskRepository.refresh(c);
        if(!c.getTask().getDeadLineTime().isBefore(c.getCompleteTime())){  //截止时间没有超出完成时间
            return "回复成功！";
        }else{
            return "已超出规定回复时间，任务完成失败！";
        }
    }
    public List<String> f(Set<String> a){
        List<String> tmp=new ArrayList<>();
        tmp.addAll(a);
        return tmp;
    }
    public Map getTaskUser(int cid) {
        List<UserTask> list = usertaskRepository.find(cid);
        Task task = taskRepository.find(cid);
        Set<String> time=new TreeSet<>();
        Set<String> intime=new TreeSet<>();
        Set<String> uncomp=new TreeSet<>();
        List<Task> tt = taskRepository.listAll();
        List<User> userList = userRepository.listAll();
        for(UserTask i:list){
            if(!task.getDeadLineTime().isBefore(i.getCompleteTime())){  //截止时间没有超出完成时间
                time.add(i.getUser().getName());
            }
            else if(!time.contains(i.getUser().getName())){
                intime.add(i.getUser().getName());
            }
        }
        for(User i:userList){
            if(!(time.contains(i.getName())||intime.contains(i.getName()))){
                uncomp.add(i.getName());
            }
        }
        return Map.of("time",f(time),"noTime",f(intime),"noComp",f(uncomp));
    }
}
