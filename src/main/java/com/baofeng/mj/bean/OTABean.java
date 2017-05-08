package com.baofeng.mj.bean;

import java.util.List;

/**OTA更新bean
 * Created by muyu on 2016/6/21.
 */
public class OTABean {


    /**
     * status : true
     * code : 100000
     * message : 操作成功
     * data : [{"upgrade_name":"12345","upgrade_download":"http://static.platform.mojing.cn/ota/201606/a3147a96bddeea850a59a771f39ba467.zip","upgrade_md5":"a3147a96bddeea850a59a771f39ba467","upgrade_version":"5","up_way":"1","upgrade_desc":"1","upgrade_provision":"1"},{"upgrade_name":"hello","upgrade_download":"http://static.platform.mojing.cn/ota/201606/a3147a96bddeea850a59a771f39ba467.zip","upgrade_md5":"a3147a96bddeea850a59a771f39ba467","upgrade_version":"4","up_way":"1","upgrade_desc":"2","upgrade_provision":"3"}]
     */

    private boolean status;
    private int code;
    private String message;
    /**
     * upgrade_name : 12345
     * upgrade_download : http://static.platform.mojing.cn/ota/201606/a3147a96bddeea850a59a771f39ba467.zip
     * upgrade_md5 : a3147a96bddeea850a59a771f39ba467
     * upgrade_version : 5
     * up_way : 1
     * upgrade_desc : 1
     * upgrade_provision : 1
     */

    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String upgrade_name;
        private String upgrade_download;
        private String upgrade_md5;
        private String upgrade_version;
        private int up_way;
        private String upgrade_desc;
        private int upgrade_provision;

        public String getUpgrade_name() {
            return upgrade_name;
        }

        public void setUpgrade_name(String upgrade_name) {
            this.upgrade_name = upgrade_name;
        }

        public String getUpgrade_download() {
            return upgrade_download;
        }

        public void setUpgrade_download(String upgrade_download) {
            this.upgrade_download = upgrade_download;
        }

        public String getUpgrade_md5() {
            return upgrade_md5;
        }

        public void setUpgrade_md5(String upgrade_md5) {
            this.upgrade_md5 = upgrade_md5;
        }

        public String getUpgrade_version() {
            return upgrade_version;
        }

        public void setUpgrade_version(String upgrade_version) {
            this.upgrade_version = upgrade_version;
        }

        public int getUp_way() {
            return up_way;
        }

        public void setUp_way(int up_way) {
            this.up_way = up_way;
        }

        public String getUpgrade_desc() {
            return upgrade_desc;
        }

        public void setUpgrade_desc(String upgrade_desc) {
            this.upgrade_desc = upgrade_desc;
        }

        public int getUpgrade_provision() {
            return upgrade_provision;
        }

        public void setUpgrade_provision(int upgrade_provision) {
            this.upgrade_provision = upgrade_provision;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "upgrade_name='" + upgrade_name + '\'' +
                    ", upgrade_download='" + upgrade_download + '\'' +
                    ", upgrade_md5='" + upgrade_md5 + '\'' +
                    ", upgrade_version='" + upgrade_version + '\'' +
                    ", up_way=" + up_way +
                    ", upgrade_desc='" + upgrade_desc + '\'' +
                    ", upgrade_provision=" + upgrade_provision +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OTABean{" +
                "status=" + status +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}


