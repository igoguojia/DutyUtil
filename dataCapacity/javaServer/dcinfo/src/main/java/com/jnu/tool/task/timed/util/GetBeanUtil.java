package com.jnu.tool.task.timed.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author duya001
 */
@Component
public class GetBeanUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (GetBeanUtil.applicationContext == null) {
            GetBeanUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 返回ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 返回bean
     *
     * @param beanName beanName
     * @return bean
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 获取bean
     *
     * @param c c
     * @param <T> 泛型
     * @return bean
     */
    public static <T> T getBean(Class<T> c) {
        return applicationContext.getBean(c);
    }

    /**
     * 获取bean
     * @param c c
     * @param  name 名称
     * @param <T> 泛型
     * @return T 泛型
     */
    public static <T> T getBean(String name, Class<T> c) {
        return getApplicationContext().getBean(name, c);
    }
}
