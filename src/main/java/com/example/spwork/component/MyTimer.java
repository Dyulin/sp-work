package com.example.spwork.component;

import com.example.spwork.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTimer {
    @Autowired
    private ExamService examService;
    @Scheduled(cron = " 0 0 12 * * ?")
    public void sendMessage(){
        examService.sendMessageSche();
    }
    @Scheduled(cron="0 0 0/12 * * ?")
    public void updateState(){
        examService.updateState();
    }
}
