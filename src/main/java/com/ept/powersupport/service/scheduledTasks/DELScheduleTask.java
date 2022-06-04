package com.ept.powersupport.service.scheduledTasks;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@Slf4j
public class DELScheduleTask {
    public void delTask(String jobName, String TRIGGER_GROUP_NAME, String JOB_GROUP_NAME){

        TriggerKey triggerKey = TriggerKey.triggerKey(
                jobName, TRIGGER_GROUP_NAME);
        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        try {

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            Trigger trigger = (Trigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                log.error("[Schedule Tasks :: TRIGGER错误]");
            }
            sched.pauseTrigger(triggerKey);;// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            sched.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("[Schedule Tasks :: 倒计时停止] join_id = {}", jobName.substring(3));
    }
}
