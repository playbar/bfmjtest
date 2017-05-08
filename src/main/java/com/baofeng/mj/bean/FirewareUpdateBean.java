package com.baofeng.mj.bean;

import com.baofeng.mj.business.firewarebusiness.FirewareBusiness;

import java.util.List;

/**
 * Created by zhanglei1 on 2016/8/4.
 */
public class FirewareUpdateBean {
    /*
        "status": true,
        "code": "100000",
        "message": "操作成功",
        "data": [{
            "sjbbbh": "3",
            "xzdz": "http:\/\/dl.mojing.cn\/fsdf.bin",
            "sjfs": "3"
        }]
    */
    private boolean status;
    private int code;
    private String message;
    private List<FirewareUpdateData> data;
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public List<FirewareUpdateData> getData() {
        return data;
    }

    public void setData(List<FirewareUpdateData> datas) {
        this.data = datas;
    }


    public static class FirewareUpdateData{
        private int sjbbbh;
        private String xzdz;
        private int sjfs;

        public String getXzdz() {
            return xzdz;
        }

        public void setXzdz(String xzdz) {
            this.xzdz = xzdz;
        }

        public int getSjbbbh() {
            return sjbbbh;
        }

        public void setSjbbbh(int sjbbbh) {
            this.sjbbbh = sjbbbh;
        }

        public int getSjfs() {
            return sjfs;
        }

        public void setSjfs(int sjfs) {
            this.sjfs = sjfs;
        }

        @Override
        public String toString() {
            return "FirewareUpdateData{" +
                    "sjbbbh=" + sjbbbh +
                    ", xzdz='" + xzdz + '\'' +
                    ", sjfs=" + sjfs +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FirewareUpdateBean{" +
                "status=" + status +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", datas=" + data +
                '}';
    }
}
