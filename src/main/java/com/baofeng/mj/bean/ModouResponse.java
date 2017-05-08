package com.baofeng.mj.bean;

/**
 * Created by zhaominglei on 2016/5/18.
 */
public class ModouResponse {
    //1兑换码成功 0兑换失败
    public int status;
    public int modou;
    public String msg;


    @Override
    public String toString() {
        return "ModouResponse{" +
                "status=" + status +
                ", modou=" + modou +
                ", msg='" + msg + '\'' +
                '}';
    }
}
