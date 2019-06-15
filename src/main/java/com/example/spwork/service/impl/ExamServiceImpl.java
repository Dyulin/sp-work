package com.example.spwork.service.impl;

import com.example.spwork.Repository.ExamRepository;
import com.example.spwork.Repository.UserRepository;
import com.example.spwork.Repository.User_ExamRepository;
import com.example.spwork.component.MyAuthority;
import com.example.spwork.entity.Exam;
import com.example.spwork.entity.User;
import com.example.spwork.entity.User_Exam;
import com.example.spwork.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@Transactional
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamRepository ExamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private User_ExamRepository userexamRepository;
    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public void addExam(Exam exam, String level) {
        LocalDateTime start = exam.getStart();
        LocalDateTime end = exam.getEnd();
        Exam exam1 = ExamRepository.findByLocation(exam.getLocation());
        if (exam1 == null) {
            ExamRepository.save(exam);
        } else if (start.isAfter(exam1.getEnd()) || end.isBefore(exam1.getStart())) {
            ExamRepository.save(exam);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "所填监考时间冲突");
        }

    }
    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public String setUserExam(List<User> users, String level, int eid) {
        LocalDateTime start = ExamRepository.find(eid).getStart();
        LocalDateTime end = ExamRepository.find(eid).getStart();
        int number = ExamRepository.find(eid).getNumber();
        Exam exam=ExamRepository.find(eid);
        if (userexamRepository.CountExamByExamId(eid) >= number) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该监考已分配人数已达上限");
    }
        ArrayList<User> userlist = new ArrayList<User>();
        for (User u : users) {
            User_Exam user_exam = new User_Exam();
            user_exam.setUser(u);
            user_exam.setExam(exam);
            String name = u.getName();
            int count = userexamRepository.CountUserByExamId(u.getId(), start, end);
            if (count >= 2) {
                throw new RuntimeException(name + "已参与两场同时间监考");
            } else if (count == 1) {
                userexamRepository.save(user_exam);
                userlist.add(u);
            } else {
                userexamRepository.save(user_exam);
            }
        }
        ExamRepository.updateState(0, exam.getId());
        if (!userlist.isEmpty()) {
            String result = "";
            for (User u : userlist) {
                result.concat("账号为" + u.getAccount() +
                        "姓名为" + u.getName() +
                        "同时间段已参加一场监考，出现冲突但监考信息已保存" + "\n");
            }
            return result;
        }
        return "分配成功";
    }
    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public List<Object[]> CountEveryUserExams(String ok, String level) {
        try {
            return userexamRepository.CountEveryUserExams();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "获取信息失败");
        }
    }
    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public void sendMessage(int uid,String level,int eid) {
        List<String> list = userexamRepository.findByUser(eid);
        User user = userRepository.findById(uid).get();
        Exam exam = ExamRepository.findById(eid).get();
        String name = user.getName();
        int counts = userexamRepository.findCountById(uid);
        log.debug(name + "：您好,您有一场新的监考，监考时间为" + exam.getStart() + "至"
                + exam.getEnd() + " 地点为" + exam.getLocation() + "您共有" + counts + "场监考需参加");
        log.debug(" 本次监考所有老师为");
        for (String o : list) {
            log.debug(o + " ");
        }
    }
    @Override
    public List<Object[]> queryAllExam() {
        return userexamRepository.queryAllExam();
    }
    @Override
    public Exam queryExamDetail(int eid) {
        return ExamRepository.find(eid);
    }
    @Override //查询某监考所有老师姓名
    public List<String> queryUsersByExam(int eid) {
        return userexamRepository.findByUser(eid);
    }

    @Override//查询某监考的所有老师实体
    public List<User> queryUsers(int eid) {
        return userexamRepository.findUser(eid);
    }

    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public void delUserExam(int eid,String level) {
        userexamRepository.delUserExam(eid);
    }

    @Override
    public void sendMessageSche() {
        List<Integer> list=userexamRepository.findState0();
        for(Integer i:list)
        {
            List<String> list1 =userexamRepository.findByUser(i);
            Exam exam1 = ExamRepository.findById(i).get();
            User user1 = userRepository.findById(i).get();
            String name = user1.getName();
            int counts = userexamRepository.findCountById(i);
            log.debug(name + "：您好,您有一场新的监考，监考时间为 " + exam1.getStart() + "至"
                    + exam1.getEnd() + " 地点为" + exam1.getLocation() + "您共有" + counts + "场监考需参加");
            log.debug(" 本次监考所有老师为");
            for (String l : list1) {
                log.debug(l + " ");
            }
        }
    }

    @Override
    public void updateState() {
        LocalDateTime ldt= LocalDateTime.now();
        List<Exam> list=userexamRepository.findExamSch();
        for(Exam i:list)
        {
            if(ldt.isAfter(i.getEnd()))
            {
                ExamRepository.updateState(1,i.getId());
            }
        }
    }
}

