package com.example.spwork.Repository;

import com.example.spwork.entity.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TaskRepository extends CustomizedRepoistory<Task, Integer> {
    @Query("SELECT c FROM Task c")
    List<Task> listAll();

    @Query("SELECT c FROM Task c WHERE c.id=:tid")
    Task find(@Param("tid") int tid);
}
