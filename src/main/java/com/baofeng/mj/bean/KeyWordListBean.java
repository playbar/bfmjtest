package com.baofeng.mj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunshine on 16/9/20.
 * 搜索关键词列表实体类
 */
public class KeyWordListBean implements Serializable {
    private int total;
    private List<KeyWordItemBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<KeyWordItemBean> getList() {
        return list;
    }

    public void setList(List<KeyWordItemBean> list) {
        this.list = list;
    }
}
