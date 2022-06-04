package com.ept.powersupport.service.scheduledTasks;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;

public class PayIntimeTskMgr {
    private String join_id;
    private String timeout;

    public PayIntimeTskMgr(String join_id, String timeout){
        this.join_id = join_id;
        this.timeout = timeout;
    }

    public void startTsk() throws SchedulerException, ParseException {
        // 任务名称
        String jobName = "DEL" + join_id;
        // 任务组
        String jobGroup = "DEL" + join_id;

        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(PayIntimeTsk.class)
                .withIdentity(jobName, jobGroup)
                .build();

        // 触发名称
        String triName = "DEL" + join_id;
        // 出发表达式
//        String triPress = "*/10 * * * * ?";     //每10秒执行一次
        String triPress = "0 */1 * * * ?";     //每?分钟执行一次
        CronExpression express = new CronExpression(triPress);
        // 构建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triName, jobGroup)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(express))
                .build();

        // 创建调度器（Scheduler）
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        // 注册调度器（Scheduler）
        sched.scheduleJob(jobDetail, trigger);

        // 启动调度器（Scheduler）
        sched.start();
    }
}
