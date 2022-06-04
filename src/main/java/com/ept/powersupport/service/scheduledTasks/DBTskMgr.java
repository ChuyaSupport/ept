package com.ept.powersupport.service.scheduledTasks;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import java.text.ParseException;

@Service
@Slf4j
public class DBTskMgr {

    public void startTsk() throws SchedulerException, ParseException {
        // 任务名称
        String jobName = "eptJob";
        // 任务组
        String jobGroup = "eptJob";

        // 构建JobDetail
        JobDetail jobDetail= JobBuilder.newJob(GrpStatusCheck.class)
                .withIdentity(jobName,jobGroup)
                .build();

        // 触发名称
        String triName = "eptJob";
        // 出发表达式
//        String triPress = "*/10 * * * * ?";     //每10秒执行一次
        String triPress = "0 */1 * * * ?";     //每分钟执行一次
        CronExpression express = new CronExpression(triPress);
        // 构建触发器
        Trigger trigger= TriggerBuilder.newTrigger()
                .withIdentity(triName,jobGroup)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(express))
                .build();

        // 创建调度器（Scheduler）
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        // 注册调度器（Scheduler）
        sched.scheduleJob(jobDetail,trigger);

        // 启动调度器（Scheduler）
        sched.start();
    }
}