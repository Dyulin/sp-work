package com.example.spwork.entity;

public  enum AccountLevel{
        USER("user"),//普通用户
        ADMIN("admin"),//管理员
        SUPERADMIN("superadmin"); //超级管理员
        private final String name;
        AccountLevel(String name){
            this.name = name;
        }
}
