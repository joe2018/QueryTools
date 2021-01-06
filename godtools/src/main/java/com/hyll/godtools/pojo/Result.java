package com.hyll.godtools.pojo;

import com.hyll.godtools.config.ResultCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    /**
     * 是否响应成功
     */
    private Boolean success;
    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 错误信息
     */
    private String message;

    /**
     * 总数量
     */
    private long total;

    // 构造器开始
    /**
     * 无参构造器(构造器私有，外部不可以直接创建)
     */
    private Result() {
        this.code = 200;
        this.message = "执行成功";
        this.success = true;
    }
    /**
     * 有参构造器
     * @param obj 返回值
     */
    private Result(T obj) {
        this.code = 200;
        this.message = "执行成功";
        this.data = obj;
        this.success = true;
    }

    /**
     * 有参构造器
     * @param resultCode 枚举值
     */
    private Result(ResultCode resultCode) {
        this.success = false;
        this.code = resultCode.code();
        this.message = resultCode.message();
    }


    /**
     * 有参构造器
     * @param obj 返回值
     */
    private Result(T obj,long total) {
        this.code = 200;
        this.message = "执行成功";
        this.data = obj;
        this.success = true;
        this.total = total;
    }
    // 构造器结束

    /**
     * 通用返回成功（没有返回结果）
     * @param
     * @return 成功返回
     */
    public static<T> Result<T> success(){
        return new Result<T>();
    }

    /**
     * 返回成功（有返回结果）
     * @param data 返回值
     * @param <T> Object
     * @return 返回值
     */
    public static<T> Result<T> success(T data){
        return new Result<T>(data);
    }

    public static<T> Result<T> success(T data,long total){
        return new Result<T>(data,total);
    }

    /**
     * 通用返回失败
     * @param resultCode 失败的枚举
     * @param <T> 返回类
     * @return 返回值
     */
    public static<T> Result<T> failure(ResultCode resultCode){
        return  new Result<T>(resultCode);
    }

//    public Boolean getSuccess() {
//        return success;
//    }
//
//    public void setSuccess(Boolean success) {
//        this.success = success;
//    }
//
//    public Integer getCode() {
//        return code;
//    }
//
//    public void setCode(Integer code) {
//        this.code = code;
//    }
//
//    public T getData() {
//        return data;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    @Override
//    public String toString() {
//        return "HttpResult{" +
//                "success=" + success +
//                ", code=" + code +
//                ", data=" + data +
//                ", message='" + message + '\'' +
//                '}';
//    }
}
