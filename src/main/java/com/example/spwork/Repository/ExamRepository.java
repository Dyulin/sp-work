package com.example.spwork.Repository;

import com.example.spwork.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ExamRepository extends JpaRepository <Exam,Integer>{
@Query("SELECT u FROM Exam u WHERE u.location=:location")
Exam findByLocation(@Param("location") String location);
@Query("SELECT u FROM Exam u WHERE u.id=:id")
Exam find(@Param("id") int id);

@Query("SELECT  e from Exam e where e.state=-1")//查找未被分配的监考信息
List<Exam> findExamByState();

    @Query("select ue" +
            " from Exam ue order by ue.id desc")
    List<Exam> queryAllExam();

@Modifying
@Query("UPDATE Exam e SET e.state=:state WHERE e.id=:id")
int updateState(@Param("state") int state,@Param("id") int id);
}
