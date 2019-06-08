package com.example.spwork.component;

import com.example.spwork.entity.AccountLevel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({TYPE,METHOD})
public @interface MyAuthority {
  MyAuthorityType[] value() default MyAuthorityType.USER;
  public static enum MyAuthorityType {
    USER, ADMIN, SUPERADMIN;
  }
}

