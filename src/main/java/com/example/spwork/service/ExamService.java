package com.example.spwork.service;

import com.example.spwork.entity.Exam;
import com.example.spwork.entity.User;
import java.util.List;


public interface ExamService {
    void addExam(Exam exam,String level);
    String setUserExam(List<User> users,String level,int eid);
    List<Object[]> CountEveryUserExams(String ok, String level);
    void sendMessage(int uid,String level,int eid);
    List<Object[]> queryAllExam();
    Exam queryExamDetail(int eid);
    List<String> queryUsersByExam(int eid);
    void delUserExam(int eid,String level);
    List<User> queryUsers(int eid);
    void sendMessageSche();
    void updateState();
}
