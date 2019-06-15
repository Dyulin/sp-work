package com.example.spwork.Repository;


import com.example.spwork.entity.Exam;
import com.example.spwork.entity.User;
import com.example.spwork.entity.User_Exam;
import com.sun.jdi.ObjectCollectedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public interface User_ExamRepository extends JpaRepository<User_Exam,Integer> {
    //该监考已分配多少人
    @Query("select count(u.exam) from User_Exam u where u.exam.id = :id")
    int CountExamByExamId(@Param("id") int examId);
    //在当前时间段 某人有多少监考
    @Query("select count(u.exam) from User_Exam u where u.user.id = :id AND("+
            "(u.exam.start>:start AND u.exam.start<=:end) OR  " +
            "(u.exam.end>:start AND u.exam.end<=:end) )")
    int CountUserByExamId(@Param("id") int userId, @Param("start") LocalDateTime start
    , @Param("end") LocalDateTime end);
    //查询所有人已分配未完成的监考次数
    @Query("select  u.id,u.account,u.name,count(ue.exam.id) " +
            "from User u left join User_Exam ue on ue.user.id=u.id where " +
            "ue.exam.state=0 group by u.id")
    List<Object[]> CountEveryUserExams();
    //查询某监考的所有老师姓名
    @Query("select ue.user.name" +
            " from User_Exam ue where ue.exam.id=:eid")
    List<String> findByUser(@Param("eid") int eid);
    //某人有多少已分配未完成的监考
    @Query("select count(ue.user) from  User_Exam  ue where ue.user.id=:id and ue.exam.state=0")
    int findCountById(@Param("id") int id);
    //查询所有监考的用户id 姓名；监考id，监考课程，监考状态
    @Query("select ue.id,ue.user.id,ue.user.name,ue.exam.id,ue.exam.course,ue.exam.state" +
            " from User_Exam ue order by ue.id desc")
    List<Object[]> queryAllExam();
    @Modifying
    @Query("delete  from User_Exam ue where ue.exam.id=:eid")
    void delUserExam(int eid);
    @Query("select ue from User_Exam ue where ue.exam.id=:id")
    List<User> findUser(int id);
    @Query("select ue.user.id from User_Exam ue where ue.exam.state=0")
    List<Integer> findState0();
    @Query("Select ue.exam from User_Exam ue where ue.exam.state=0")
    List<Exam> findExamSch();
}
