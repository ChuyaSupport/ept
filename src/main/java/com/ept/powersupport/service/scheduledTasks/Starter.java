package com.ept.powersupport.service.scheduledTasks;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.text.ParseException;

@Component
@Slf4j
public class Starter implements CommandLineRunner {

    @Autowired
    private DBTskMgr DBTskMgr;

    @Override
    public void run(String... args) throws SchedulerException, ParseException {

        DBTskMgr.startTsk();
        log.info("[Scheduled Tasks :: 计划任务已启动]");
    }
}
