package com.jnu.tool.task.timed.util;

import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import com.jnu.tool.task.timed.service.api.TimedTaskLogService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author duya001
 */
@Component
public class MyCmd implements Job {
    private final Logger LOGGER = Logger.getLogger("com.jun.tool.task.timed.util.MyCmd");

    @Autowired
    private TimedTaskLogService timedTaskLogService;

    /**
     * 需要传过来的参数只有cmd命令
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String task = dataMap.getString("task");
        TimedTaskLog log = (TimedTaskLog) dataMap.get("log");
        Runtime runtime = Runtime.getRuntime();

        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);
        try {
            runtime.exec(new String[]{"cmd", "/c", task});
            LOGGER.warning("执行cmd任务" + task + "成功！");
//            log.setMsgInfo("执行cmd任务" + task + "成功！");
            log.setMsgInfo("执行cmd任务" +"成功！");
            log.setSubmitTime(currentTimeString);
            timedTaskLogService.addTimedTaskLog(log);
        } catch (IOException e) {
            LOGGER.warning("执行cmd任务" + task + "失败！" + e);
//            log.setMsgInfo("执行cmd任务" + task + "失败！" + e);
            log.setMsgInfo("执行cmd任务" + "失败！" + e);
            log.setSubmitTime(currentTimeString);
            timedTaskLogService.addTimedTaskLog(log);
        }
    }
}
