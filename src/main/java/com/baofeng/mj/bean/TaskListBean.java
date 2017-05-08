package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/7.
 * 任务列表实体类
 */
public class TaskListBean implements Serializable {
    private boolean status;
    private String msg;
    private List<TaskBean> list;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<TaskBean> getList() {
        return list;
    }

    public void setList(List<TaskBean> list) {
        this.list = list;
    }
}
