package com.baofeng.mj.bean;

import java.util.List;

/**
 * Created by muyu on 2016/8/5.
 */
public class GlassesBean {


    /**
     * status : true
     * code : 100000
     * message : 操作成功
     * data : [{"glass_id":"11","glass_type":"1","glass_name":"魔镜5代","company_id":"1","product_id":"8","lens_id":"16"},{"glass_id":"10","glass_type":"1","glass_name":"魔镜RIO","company_id":"1","product_id":"9","lens_id":"17"},{"glass_id":"9","glass_type":"1","glass_name":"魔镜小D","company_id":"1","product_id":"7","lens_id":"15"},{"glass_id":"8","glass_type":"1","glass_name":"魔镜4代","company_id":"1","product_id":"3","lens_id":"12"},{"glass_id":"7","glass_type":"1","glass_name":"魔镜3代 Plus 纪念版","company_id":"1","product_id":"2","lens_id":"4"},{"glass_id":"6","glass_type":"1","glass_name":"魔镜3代 Plus A镜片","company_id":"1","product_id":"2","lens_id":"11"},{"glass_id":"5","glass_type":"1","glass_name":"魔镜3代 Plus B镜片","company_id":"1","product_id":"2","lens_id":"4"},{"glass_id":"4","glass_type":"1","glass_name":"魔镜3代","company_id":"1","product_id":"2","lens_id":"3"},{"glass_id":"3","glass_type":"1","glass_name":"魔镜2代","company_id":"1","product_id":"1","lens_id":"1"},{"glass_id":"2","glass_type":"1","glass_name":"魔镜1代","company_id":"1","product_id":"1","lens_id":"1"},{"glass_id":"1","glass_type":"1","glass_name":"小魔镜","company_id":"1","product_id":"1","lens_id":"1"}]
     */

    private boolean status;
    private String code;
    private String message;
    /**
     * glass_id : 11
     * glass_type : 1
     * glass_name : 魔镜5代
     * company_id : 1
     * product_id : 8
     * lens_id : 16
     */

    private List<DataBean> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String glass_id;
        private String glass_type;
        private String glass_name;
        private String company_id;
        private String product_id;
        private String lens_id;
        private String glass_thumb;
        private String glass_brand_id; //品牌id （根据id分组）
        private String glass_brand_name;//品牌名称
        private boolean isSelected = false;

        public String getGlass_id() {
            return glass_id;
        }

        public void setGlass_id(String glass_id) {
            this.glass_id = glass_id;
        }

        public String getGlass_type() {
            return glass_type;
        }

        public void setGlass_type(String glass_type) {
            this.glass_type = glass_type;
        }

        public String getGlass_name() {
            return glass_name;
        }

        public void setGlass_name(String glass_name) {
            this.glass_name = glass_name;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getLens_id() {
            return lens_id;
        }

        public void setLens_id(String lens_id) {
            this.lens_id = lens_id;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String getGlass_thumb() {
            return glass_thumb;
        }

        public void setGlass_thumb(String glass_thumb) {
            this.glass_thumb = glass_thumb;
        }

        public String getGlass_brand_name() {
            return glass_brand_name;
        }

        public void setGlass_brand_name(String glass_brand_name) {
            this.glass_brand_name = glass_brand_name;
        }

        public String getGlass_brand_id() {
            return glass_brand_id;
        }

        public void setGlass_brand_id(String glass_brand_id) {
            this.glass_brand_id = glass_brand_id;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "glass_id='" + glass_id + '\'' +
                    ", glass_type='" + glass_type + '\'' +
                    ", glass_name='" + glass_name + '\'' +
                    ", company_id='" + company_id + '\'' +
                    ", product_id='" + product_id + '\'' +
                    ", lens_id='" + lens_id + '\'' +
                    ", glass_thumb='" + glass_thumb + '\'' +
                    ", glass_brand_id='" + glass_brand_id + '\'' +
                    ", glass_brand_name='" + glass_brand_name + '\'' +
                    ", isSelected=" + isSelected +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GlassesBean{" +
                "status=" + status +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}