package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/10/20.
 */
public class ResultVo {
    public static final String CODE_SUCCESS = "0";
    public static final String CODE_FAILURE = "-1";
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
