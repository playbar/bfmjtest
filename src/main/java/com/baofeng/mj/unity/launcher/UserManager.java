package com.baofeng.mj.unity.launcher;

/**
 * Created by qiguolong on 2016/5/6.
 * 提供给u3d的接口
 */
public class UserManager extends AndroidManager {
    private static UserManager instance;
    private UserInfo userInfo;
    private long time = 0L;



                /**
                 * 检查用户信息
                 *
                 * @param phone
                 * @return
                 */

    private String checkUserInfo(String phone) {
        userInfo = UserUtil.readUser(phone);
        return GsonUtil.toJsonData(userInfo);
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * 获得当前user信息
     *
     * @return
     */
    public String getCurrentUser() {
        if (userInfo == null)
            userInfo = UserUtil.getCurrentUserInfo();
        if (userInfo == null)
            return testUserInfo();
        return GsonUtil.toJsonData(userInfo);
    }





    /**
     * 测试用户数据
     *
     * @return
     */
    public String testUserInfo() {
        String string = "{\"user_no\":\"11151515\",\"user_tel\":\"131456789\"," +
                "\"user_name\":\"testusers\",\"user_email\":\"0\",\"user_tel_en\":\"131456789\"," +
                "\"user_head_url\":\"http:\\/\\/sso.mojing" +
                ".cn\\/assets\\/web\\/images\\/usercenter\\/noavatar_big.jpg\"}";
        string = "";
        return string;
    }


}
