package com.ept.powersupport.service.scheduledTasks;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PayIntimeTsk implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //取消加入拼团
        System.out.println("执行任务");
    }
}
