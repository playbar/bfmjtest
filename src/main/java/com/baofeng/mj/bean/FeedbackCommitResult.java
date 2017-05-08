package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by yushaochen on 2017/1/4.
 */

public class FeedbackCommitResult implements Serializable{
    private static final long serialVersionUID = -1244244525767485282L;
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
