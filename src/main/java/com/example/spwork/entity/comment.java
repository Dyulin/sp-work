package com.example.spwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class comment {

    private int id;
    private String  account;
    private int  task;
    private String comment;    //回复
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime completeTime;  //完成时间
}
