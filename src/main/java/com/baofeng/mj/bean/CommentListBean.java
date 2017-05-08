package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/15.
 * 资源评论信息列表
 */
public class CommentListBean implements Serializable {
    private String total_scores;
    private String avg_scores;
    private String people_count;
    private List<CommentBean> data_list;

    public List<CommentBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<CommentBean> data_list) {
        this.data_list = data_list;
    }

    public String getTotal_scores() {
        return total_scores;
    }

    public void setTotal_scores(String total_scores) {
        this.total_scores = total_scores;
    }

    public String getAvg_scores() {
        return avg_scores;
    }

    public void setAvg_scores(String avg_scores) {
        this.avg_scores = avg_scores;
    }

    public String getPeople_count() {
        return people_count;
    }

    public void setPeople_count(String people_count) {
        this.people_count = people_count;
    }
}
