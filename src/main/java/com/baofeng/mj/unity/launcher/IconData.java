package com.baofeng.mj.unity.launcher;

public class IconData {

    /**
     * 应用图标字节数据
     */
    private byte[] mIconArray;

    public IconData(byte[] icon) {
        this.mIconArray = icon;
    }

    /**
     * 获取应用图标数据
     * @return 图标的字节数组
     */
    public byte[] getIconData() {
        return mIconArray;
    }
}
