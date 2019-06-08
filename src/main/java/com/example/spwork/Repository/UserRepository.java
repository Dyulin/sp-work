package com.example.spwork.Repository;


import com.example.spwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Integer> {
    @PersistenceContext
    @Query("SELECT password FROM User  WHERE account=:account")
    String  find(@Param("account") String account);
    @Query("SELECT level FROM  User  WHERE account=:account")
    String  findLevel(@Param("account") String account);
}
