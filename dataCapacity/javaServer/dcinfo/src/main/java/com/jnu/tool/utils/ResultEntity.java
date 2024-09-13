package com.jnu.tool.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author duya001
 */
public class ResultEntity<T> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 用来封装当前请求处理的结果是成功还是失败
     */
    private Boolean success;

    /**
     * 请求处理失败时返回的错误消息
     */
    private String msg;

    /**
     * 要返回的数据
     */
    private T data;

    /**
     * 请求处理成功且不需要返回数据时使用的工具方法
     * @return ResultEntity
     */
    public static <Type> ResultEntity<Type> successWithoutData(){
        return new ResultEntity<>(true, null, null);
    }

    /**
     * 请求处理成功且需要返回数据时使用的工具方法
     * @return ResultEntity
     */
    public static <Type> ResultEntity<Type> successWithData(Type data){
        return new ResultEntity<>(true, null, data);
    }

    /**
     * 请求处理成功且需要返回数据与消息时使用的工具方法
     * @return ResultEntity
     */
    public static <Type> ResultEntity<Type> successWithDataMsg(String message, Type data){
        return new ResultEntity<>(true, message, data);
    }

    /**
     * 请求处理失败使用的工具方法
     * @param message 失败的错误消息
     * @return ResultEntity
     */
    public static <Type> ResultEntity<Type> failWithoutData(String message){
        return new ResultEntity<>(false, message, null);
    }

    public ResultEntity() {
    }

    public ResultEntity(Boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String returnResult() throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "ResultEntity{" +
                "success='" + success + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
