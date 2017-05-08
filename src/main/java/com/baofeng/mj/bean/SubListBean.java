package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanyang on 2016/6/21.
 * 订阅专辑列表实体类
 */
public class SubListBean implements Serializable {
    private List<SubBean> list;

    public List<SubBean> getList() {
        return list;
    }

    public void setList(List<SubBean> list) {
        this.list = list;
    }
}
