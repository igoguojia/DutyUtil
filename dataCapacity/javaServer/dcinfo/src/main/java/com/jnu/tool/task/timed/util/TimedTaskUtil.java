package com.jnu.tool.task.timed.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.iceolive.util.StringUtil;
import com.jnu.tool.utils.ResultEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author duya001
 */
public class TimedTaskUtil {
    public static final Logger LOGGER = Logger.getLogger("com.jnu.tool.task.timed.util.TimedTaskUtil");

    private Class<?> aClass;
    Method[] methods;

    public Class<?> getaClass() {
        return aClass;
    }

    public String[] getClassAndMethodName(String task) {
        if (StringUtil.isEmpty(task)) {
            return null;
        }
        char[] chars = task.toCharArray();
        int pos = 0;
        int index;
        for (index = chars.length - 1; index >= 0; index--) {
            if (chars[index] == '.') {
                pos = index;
                break;
            }
        }
        if (index <= 0) {
            return null;
        }
        // 全类名
        String className = task.substring(0, pos);
        // 方法名
        String methodName = task.substring(pos + 1);
        return new String[]{className, methodName};
    }

    private boolean isLegalClass(String className) {
        try {
            aClass = Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public String isLegalMethod(String className, String methodName) throws JsonProcessingException {
        ResultEntity<Object> resultEntity;
        if (StringUtil.isEmpty(className) || StringUtil.isEmpty(methodName)) {
            resultEntity = ResultEntity.failWithoutData("非法任务！");
            LOGGER.warning("非法任务！");
            return resultEntity.returnResult();
        }
        if (!isLegalClass(className)) {
            resultEntity = ResultEntity.failWithoutData("非法类名！");
            LOGGER.warning("非法类名！");
            return resultEntity.returnResult();
        }
        methods = aClass.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return "";
            }
        }
        resultEntity = ResultEntity.failWithoutData("非法方法名！");
        LOGGER.warning("非法方法名！");
        return resultEntity.returnResult();
    }

    public Class<?>[] handleParams(List<Map<String, Object>> params) {
        List<Class<?>> list = new ArrayList<>(10);
        for (Map<String, Object> param : params) {
            String[] keyArray = param.keySet().toArray(new String[0]);
            if (keyArray.length != 1) {
                return null;
            }
            String key = keyArray[0];
            Class<?> aClass = ParamConstant.getClass(key);
            if (aClass == null) {
                return null;
            } else {
                list.add(aClass);
            }
        }
        return list.toArray(new Class[0]);
    }
}
