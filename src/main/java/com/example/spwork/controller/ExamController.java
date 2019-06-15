package com.example.spwork.controller;

import com.example.spwork.Repository.User_ExamRepository;
import com.example.spwork.component.EncryptorComponent;
import com.example.spwork.entity.Exam;
import com.example.spwork.entity.User;
import com.example.spwork.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class ExamController {
    @Autowired
    private EncryptorComponent encryptorComponent;
    @Autowired
    private ExamService examService;
    @PostMapping("/addExam") //新增监考信息
    public Map addExam(@RequestBody Exam exam,HttpServletRequest request){
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        examService.addExam(exam,level);
        return Map.of("code","200","message","新增监考成功");
    }
    @PostMapping("/setUserExam/{eid}")  //分配监考
    public Map setUserExam(@RequestBody List<User> users,@PathVariable int eid, HttpServletRequest request){
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        String data=examService.setUserExam(users,level,eid);
        return Map.of("code","200","message","分配监考成功","data",data);
    }
    @GetMapping("/CountEveryUserExams")  //获取每一个用户监考次数
    public Map CountEveryUserExams(HttpServletRequest request){
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        List<Object[]> data=examService.CountEveryUserExams(level,level);
        return Map.of("code","200","message","获取用户监考次数成功","data",data);
    }
    @GetMapping("/sendMessage/{uid}/{eid}") //向分配后的教师发送短信
    public Map sendMessage(@PathVariable int uid,@PathVariable int eid,HttpServletRequest request){
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        examService.sendMessage(uid,level,eid);
        return Map.of("code","200","message","发送短信成功");
    }
    @PostMapping("/updateExam/{eid}")//重新分配监考
    public Map updateExam(@RequestBody List<User> users,@PathVariable int eid, HttpServletRequest request)
    {
        String token=request.getHeader("Authorization");
        String level=(String)(encryptorComponent.decrypt(token).get("level"));
        List<User> userList= examService.queryUsers(eid);
        examService.delUserExam(eid,level);
        try{examService.setUserExam(users,level,eid);}
        catch (Exception e)
        {
            examService.setUserExam(userList, level,eid );
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "请求错误：某人已经参与同时段两场监考，请重新进行分配");
        }
        return Map.of("code","200","message","修改分配成功");
    }
    @GetMapping("/listAllExam") //查询所有监考的概要信息
    public Map  listAllExam() {
        List<Object[]> data = examService.queryAllExam();
        return Map.of("code", "200", "message", "获取所有监考信息成功", "data", data);
    }
    @GetMapping("/ListExamDetail/{eid}") //查询某个监考的具体信息
    public  Map listExamDetail(@PathVariable int eid)
    {
        Exam exam= examService.queryExamDetail(eid);
        int state=exam.getState();
        if(state!=0){
            List <String> list=examService.queryUsersByExam(eid);
        return Map.of("code","200","message",
                "获取所有监考信息成功","data",exam,"data2",list);
        }
        return Map.of("code","200","message",
                "获取所有监考信息成功","data",exam);
    }

}



















