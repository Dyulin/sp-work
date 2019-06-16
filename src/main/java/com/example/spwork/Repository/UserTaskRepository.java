package com.example.spwork.Repository;

import com.example.spwork.entity.UserTask;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRepository extends CustomizedRepoistory<UserTask, Integer> {
    @Query("SELECT c FROM UserTask c WHERE c.task.id=:tid")
    List<UserTask> list(@Param("tid") int tid);

    /*@Query("SELECT c.user FROM UserTask c WHERE c.task.id=:tid")
    List<User> find(@Param("tid") int tid);*/

    @Query("SELECT c FROM UserTask c WHERE c.task.id=:tid")
    List<UserTask> find(@Param("tid") int tid);

    @Query("SELECT count(c) FROM UserTask c WHERE c.task.id=:tid AND c.user.id=:uid")
    int judge(@Param("tid") int tid, @Param("uid") int uid);
}
