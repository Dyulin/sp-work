package com.example.spwork.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Aspect
public class AuthorityAspect {
    @Autowired
    private EncryptorComponent encryptorComponent;
    // 任何包含MyAuthority注释的类或方法，顺序必须是先判断方法再判断类型
    @Around("@within(myAuthority) || @annotation(myAuthority)")
    public Object interecptorTarget(ProceedingJoinPoint joinpoint, MyAuthority myAuthority) throws Throwable {
        Object[] args=joinpoint.getArgs();
        String  level = (String)args[1];
        log.debug("你是"+level);
        List<MyAuthority.MyAuthorityType> myAuthorities = new ArrayList<>();
        if(level.equals("SUPERADMIN")){
            myAuthorities.add(MyAuthority.MyAuthorityType.SUPERADMIN);
            myAuthorities.add(MyAuthority.MyAuthorityType.ADMIN);
        }else if(level.equals("ADMIN")){
            myAuthorities.add(MyAuthority.MyAuthorityType.ADMIN);
        }else if (level.equals("USER"))
        {
            myAuthorities.add(MyAuthority.MyAuthorityType.USER);
        }
        // 如果是类型注释
        Optional.ofNullable(myAuthority)
                // 类型注释@annotation(myAuthority)会将myAuthority参数至空，因此需反射获取类注释
                .or(() -> {
                    MyAuthority m = joinpoint.getTarget().getClass().getAnnotation(MyAuthority.class);
                    return Optional.of(m);
                })
                // 比较用户实际权限
                .ifPresent(m->{
                    for (MyAuthority.MyAuthorityType t : m.value()) {
                    log.debug("方法级别为"+t);
                    log.debug("我的权限级别为"+myAuthorities.toString());
                    if(!myAuthorities.contains(t))
                    {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"无权限");
                    }
                    }
                });
        return joinpoint.proceed();
    }
}
