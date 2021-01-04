package com.hyll.godtools.config;

/**
 * 返回状态嘛
 */
public enum ResultCode {
    /* 文件提交状态 */
    FILE_IS_BLANK(1001,"文件为空"),
    FILE_WRITE_FAILURE(1002,"文件内容错误"),
    FILE_TYPE_ERROR(1003,"文件类型错误"),
    FILE_WRITE_ERROR(1004,"写入异常"),
    FILE_ALREADY_EXISTS(1005,"文件已存在"),
    OPERATION_FAILURS(1006,"操作失败");


    /**
     * 响应码状态
     */
    private final Integer code;
    private final String message;

    ResultCode(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer code(){
        return this.code;
    }

    public String message(){
        return this.message;
    }

}
