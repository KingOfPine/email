package com.lsy.send.email.utils;

/**
 * Created by  liangsongying on 2018/3/2.
 */
public class WebResult {
    private int code;
    private String message;
    private Object data;


    public WebResult(int code, String message, Object data) {
        this.code=code;
        this.message = message;
        this.data = data;
    }

    public static WebResult success(Object data) {
        return new WebResult(0, null, data);
    }

    public static WebResult failure(String message) {
        return new WebResult(1, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
