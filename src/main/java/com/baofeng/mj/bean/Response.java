package com.baofeng.mj.bean;

/**
 * Created by zhaominglei on 2016/5/16.
 */
public class Response<T> {
    public boolean status;
    public String msg;
    //区分不同 response 信息
    public String message;
    public T data;
    public int code;

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
