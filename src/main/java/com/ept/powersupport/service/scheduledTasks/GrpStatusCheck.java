package com.ept.powersupport.service.scheduledTasks;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import java.io.IOException;


public class GrpStatusCheck implements Job {

    @Override
    public void execute(JobExecutionContext context) {

        String url = "http://localhost/grpStatusUpdate";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}