package com.baofeng.mj.unity.launcher;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;


public class IconCache {

    private static LruCache<String, IconData> mLruCache;

    private PackageManager mPackageManager;

    public IconCache(PackageManager manager) {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 16);
        mLruCache = new LruCache<>(maxSize);
        mPackageManager = manager;
    }

    public IconData getIconData(String packageName) {
        IconData data = null;
        if (mLruCache.get(packageName) != null) {
            data = mLruCache.get(packageName);
        } else {
            Drawable icon = Utils.getIconDrawableFromPackageName(packageName,
                    mPackageManager);
            byte[] iconArray = Utils.drawableToByte(icon);
            data = new IconData(iconArray);
            mLruCache.put(packageName, data);
        }
        return data;
    }

    public void remove(String pkgName) {
        mLruCache.remove(pkgName);
    }


}
