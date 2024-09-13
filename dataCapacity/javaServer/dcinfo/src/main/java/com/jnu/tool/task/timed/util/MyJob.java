package com.jnu.tool.task.timed.util;

import com.jnu.tool.task.timed.pojo.TimedTaskLog;
import com.jnu.tool.task.timed.service.api.TimedTaskLogService;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author duya001
 */
@Component
public class MyJob implements Job {
    private final Logger LOGGER = Logger.getLogger("com.jnu.tool.task.timed.util.MyJob");

    public static void print() {
        System.out.println("HelloWorld!");
    }

    public void print(String str) {
        System.out.println(str);
    }

    @Autowired
    private TimedTaskLogService timedTaskLogService;

    /**
     * 需要传过来的参数有：params、className、methodName、aClass、paramsType
     */
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        List<Map<String, Object>> params = (List<Map<String, Object>>) dataMap.get("params");
        String methodName = dataMap.getString("methodName");
        Class<?> aClass = (Class<?>) dataMap.get("aClass");
        Class<?>[] paramsType = (Class<?>[]) dataMap.get("paramsType");
        TimedTaskLog log = (TimedTaskLog) dataMap.get("log");
        Object instance;

        Date currentTime = new Date();
        // 日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = format.format(currentTime);

        // 获取实例
        try {
            instance = GetBeanUtil.getBean(aClass);
        } catch (NoSuchBeanDefinitionException e) {
            try {
                instance = aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e1) {
                LOGGER.log(Level.WARNING, "实例化异常！");
                log.setMsgInfo("实例化异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
                return;
            }
        }
        // 获取要执行的方法
        Method method;
        if (params.isEmpty()) {
            try {
                // 无参数方法获取
                method = aClass.getMethod(methodName);
                // 执行方法
                method.invoke(instance);
                LOGGER.log(Level.INFO, "执行成功！");
                log.setMsgInfo("执行成功!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.WARNING, "获取方法异常！");
                log.setMsgInfo("获取方法异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "执行方法异常！");
                log.setMsgInfo("获取方法异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            }
        } else {
            try {
                // 带参方法获取
                method = aClass.getMethod(methodName, paramsType);
                List<Object> paramList = new ArrayList<>(10);
                for (Map<String, Object> param : params) {
                    for (String s : param.keySet()) {
                        paramList.add(param.get(s));
                    }
                }
                // 执行方法
                method.invoke(instance, paramList.toArray());
                LOGGER.log(Level.INFO, "执行成功！");
                log.setMsgInfo("执行成功!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.WARNING, "获取方法异常！");
                log.setMsgInfo("获取方法异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "执行方法异常！");
                log.setMsgInfo("获取方法异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "参数异常！");
                log.setMsgInfo("获取方法异常!");
                log.setSubmitTime(currentTimeString);
                timedTaskLogService.addTimedTaskLog(log);
            }
        }
    }
}
