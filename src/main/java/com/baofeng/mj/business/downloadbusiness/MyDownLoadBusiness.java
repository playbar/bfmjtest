package com.baofeng.mj.business.downloadbusiness;

import android.app.Activity;

import com.baofeng.mj.bean.AppExtraBean;
import com.baofeng.mj.bean.ContentInfo;

import java.util.HashMap;

/**抽取下载业务
 * Created by yum on 16/7/12.
 */
public class MyDownLoadBusiness extends DownLoadBusiness<ContentInfo> {

    public MyDownLoadBusiness(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public String getResId(ContentInfo object) {
        return object.getRes_id();
    }

    @Override
    public String getPackageName(ContentInfo object) {
        AppExtraBean appExtraBean = object.getApp_extra();
        if (appExtraBean != null) {
            return appExtraBean.getPackage_name();
        }
        return null;
    }

    @Override
    public HashMap<String, Object> getRequestParams(ContentInfo object) {
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("resType", object.getType());//资源类型
        requestParams.put("detailUrl", object.getUrl());//详情页url
        requestParams.put("parentResId", object.getParentResId());// 组件id 用于报数
        requestParams.put("layoutType", object.getLayout_type());// 组件类型 用于报数
        requestParams.put("resId", object.getRes_id());// 资源id 用于报数
        requestParams.put("title", object.getTitle());// title 用于报数
        AppExtraBean appExtraBean = object.getApp_extra();
        if(appExtraBean != null){
            requestParams.put("packageName", appExtraBean.getPackage_name());// 包名
            requestParams.put("versionCode", appExtraBean.getVersion_code());// 版本号
            requestParams.put("fileSize",appExtraBean.getFilesize());
        }
        return requestParams;
    }

    @Override
    public String getTitle(ContentInfo object) {
        return object.getTitle();
    }

}
