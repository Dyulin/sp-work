package com.example.spwork.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        log.debug("Get token from request is {} ", token);
        //String dtoken=encryptorComponent.decrypt(token).get("account");
        String account = "";
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        if (token != null && token.length() != 0) {
            account = jedis.get(token);
            if (account != null && !account.trim().equals("")) {
                Long tokeBirthTime = Long.valueOf(jedis.get(token + account));
                log.info("token Birth time is: {}", tokeBirthTime);
                Long diff = System.currentTimeMillis() - tokeBirthTime;
                log.info("token is exist : {} ms", diff);
                if (diff > 1000 * 60 * 20) {
                    jedis.expire(account, 60 * 60 * 2);
                    jedis.expire(token, 60 * 60 * 2);
                    log.info("Reset expire time success!");
                    Long newBirthTime = System.currentTimeMillis();
                    jedis.set(token + account, newBirthTime.toString());
                    jedis.expire(token+account, 60 * 60 * 2);
                    jedis.close();
                }
                return true;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录！");
    }
}
