package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * Created by hanyang on 2016/6/7.
 * 任务实体类
 */
public class TaskBean implements Serializable {
    private String aid;
    private String modou;
    private String activity_name;
    private String activity_desc;
    private String start_date;
    private String end_date;
    private int hadget;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getModou() {
        return modou;
    }

    public void setModou(String modou) {
        this.modou = modou;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getActivity_desc() {
        return activity_desc;
    }

    public void setActivity_desc(String activity_desc) {
        this.activity_desc = activity_desc;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getHadget() {
        return hadget;
    }

    public void setHadget(int hadget) {
        this.hadget = hadget;
    }
}
