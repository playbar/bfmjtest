package com.baofeng.mj.bean;

/**
 * Created by muyu on 2017/3/13.
 */
public class TabOrderBean {

    /**
     * "id": 渠道唯一ID,
     "cid": 渠道号,
     "cname": 渠道名称,
     "iftab": 是否特殊排序：1否，2是
     "cdate": 创建时间,
     "udate": 修改时间
     */
    private String id;
    private String cid;
    private String cname;
    private int ifTab;
    private String cdate;
    private String udate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public int getIfTab() {
        return ifTab;
    }

    public void setIfTab(int ifTab) {
        this.ifTab = ifTab;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    @Override
    public String toString() {
        return "TabOrderBean{" +
                "id='" + id + '\'' +
                ", cid='" + cid + '\'' +
                ", cname='" + cname + '\'' +
                ", ifTab=" + ifTab +
                ", cdate='" + cdate + '\'' +
                ", udate='" + udate + '\'' +
                '}';
    }
}
