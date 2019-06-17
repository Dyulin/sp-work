package com.example.spwork.service.impl;

import com.example.spwork.Repository.ExamRepository;
import com.example.spwork.Repository.UserRepository;
import com.example.spwork.Repository.User_ExamRepository;
import com.example.spwork.component.MyAuthority;
import com.example.spwork.entity.*;
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
        LocalDateTime end = ExamRepository.find(eid).getEnd();
        int number = ExamRepository.find(eid).getNumber();
        log.debug("该监考限制人数为"+number);
        Exam exam=ExamRepository.find(eid);
        int state=exam.getState();
        if(state==1)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该监考已完成，不能进行修改");
        }
        log.debug("该监考已分配的人数为"+userexamRepository.CountExamByExamId(eid));
        log.debug("新增的人数为"+users.size());
        int number2=userexamRepository.CountExamByExamId(eid)+users.size();
        if (number2 >number) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该监考分配人数超过上限");
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
    public List<UserCountDTO> CountEveryUserExams(String ok, String level) {
            List<User> list= userRepository.listAll();
            List<UserCountDTO> list2 =new ArrayList<>();
            for(User i:list){
                int id=i.getId();
                UserCountDTO userCountDTO= new UserCountDTO();
                userCountDTO.setId(id);
                userCountDTO.setAccount(i.getAccount());
                userCountDTO.setName((i.getName()));
                userCountDTO.setCounts(userexamRepository.findCountById(id));
                list2.add(userCountDTO);            }
                return list2;
        }
    @Override
    @MyAuthority(MyAuthority.MyAuthorityType.ADMIN)
    public String sendMessage(int uid,String level,int eid) {
        //log.debug(account);
        List<String> list = userexamRepository.findByUser(eid);
        Exam exam = ExamRepository.find(eid);
        User user = userRepository.find2(uid);
        String name = user.getName();
        List<String> s = userexamRepository.findByUser(eid);
        int flag =0;
        for(String i:s){
            if(i==name){
                flag=1;
            }
        }
        if(flag==0){
            return  "该教师未被分配至此次监考，无法发送短信";
        }
        //int state=exam.getState();
        /*if(state==1||state==-1)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该教师未被分配至此次监考，无法发送短信");
        }*/
        int counts = userexamRepository.findCountById(user.getId());
        log.debug("当前有"+counts);
        log.debug(name + "：您好,您有一场新的监考，监考时间为" + exam.getStart() + "至"
                + exam.getEnd() + " 地点为" + exam.getLocation() + "您共有" + counts + "场监考需参加");
        log.debug(" 本次监考所有老师为");
        for (String o : list) {
            log.debug(o + " ");
        }
        return "success";
    }
    @Override
    public List<Exam> queryAllExam() {
        List<Exam> list = ExamRepository.queryAllExam();
        return list;
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
    @Override
    public void modifyState(int state,int id){
        ExamRepository.updateState(state, id);
    }

    @Override
    public List<Exam> findByState() {
        return  ExamRepository.findExamByState();
    }
}

