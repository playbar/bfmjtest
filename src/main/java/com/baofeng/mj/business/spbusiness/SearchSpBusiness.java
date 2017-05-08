package com.baofeng.mj.business.spbusiness;

import android.content.Context;

import com.baofeng.mj.business.publicbusiness.ConfigConstant;
import com.baofeng.mj.util.publicutil.SecurePreferences;

/**
 * Created by sunshine on 16/9/20.
 * 保存搜索记录
 */
public class SearchSpBusiness {
    private static SearchSpBusiness instance;
    private SecurePreferences securePreferences;
    private SecurePreferences.Editor editor;

    private SearchSpBusiness() {
    }

    public static SearchSpBusiness getInstance() {
        if (instance == null) {
            instance = new SearchSpBusiness();
        }
        return instance;
    }

    /**
     * 初始化SecurePreferences
     */
    private void initSecurePreferences() {
        if (securePreferences == null) {
            securePreferences = new SecurePreferences(ConfigConstant.PACKAGE_NAME + ".searchHistory", Context.MODE_PRIVATE);
            editor = securePreferences.edit();
        }
    }

    /**
     * 保存搜索记录
     *
     * @param hisJson
     */
    public void saveSearchHis(String hisJson) {
        initSecurePreferences();
        editor.putString("history", hisJson);
        editor.commit();
    }

    /**
     * 读取搜索记录
     *
     * @return
     */
    public String getSearchHis() {
        initSecurePreferences();
        return securePreferences.getString("history", "");
    }

    /**
     * 设置游戏非第一次打开状态,1为已经打开过,0为未打开过
     * @param res_id
     */
    public void setGameOpenState(String res_id) {
        initSecurePreferences();
        editor.putInt(res_id, 1);
        editor.commit();
    }
    /**
     * 获取游戏打开状态,1为已经打开过,0为未打开过
     */
    public int getGameOpenState(String res_id) {
        initSecurePreferences();
        return securePreferences.getInt(res_id,0);
    }
}