package com.example.spwork.Repository;


import com.example.spwork.entity.AccountLevel;
import com.example.spwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Integer> {
    @PersistenceContext
    @Query("SELECT u FROM User u WHERE u.account=:account")
    User  find(@Param("account") String account);
    @Query("SELECT u.level FROM User u WHERE u.account=:account")
    String  findLevel(@Param("account") String account);

    @Modifying
    @Query("UPDATE User u SET u.intro=:intro , u.name=:name " +
            ",u.phone=:phone,u.position=:position WHERE u.account=:account")
    int updateUser(@Param("intro") String intro,@Param("name")
            String name,@Param("phone") String phone,@Param("position") String position,
            @Param("account") String account);

    @Modifying
    @Query("UPDATE User u SET u.level=:level WHERE u.account=:account")
    int updateAuth(@Param("level") AccountLevel level, @Param("account") String account);

    @Modifying
    @Query("UPDATE User u SET u.password=:password WHERE u.account=:account")
    int updatePass(@Param("password") String password, @Param("account") String account);

    List<User> findAllByOrderByIdDesc();

    @Modifying
    @Query("DELETE from User u WHERE u.account=:account")
    int deleteByAccount(@Param("account") String account);

}
