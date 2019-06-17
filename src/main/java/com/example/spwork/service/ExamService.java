package com.example.spwork.service;

import com.example.spwork.entity.Exam;
import com.example.spwork.entity.User;
import com.example.spwork.entity.UserCountDTO;

import java.util.List;


public interface ExamService {
    void addExam(Exam exam,String level);
    String setUserExam(List<User> users,String level,int eid);
    List<UserCountDTO> CountEveryUserExams(String ok, String level);
    String sendMessage(int uid,String level,int eid);
    List<Exam> queryAllExam();
    Exam queryExamDetail(int eid);
    List<String> queryUsersByExam(int eid);
    void delUserExam(int eid,String level);
    List<User> queryUsers(int eid);
    void sendMessageSche();
    void updateState();
    List<Exam> findByState();
    //查找未被分配的监考信息
    void modifyState(int state,int id);
}
