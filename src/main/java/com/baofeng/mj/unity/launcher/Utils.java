package com.baofeng.mj.unity.launcher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Utils {

    private static final String TAG = Application.class.getName() + "Utils";

    public static boolean isLoadAllAppOnlyString = true;

    private static Canvas mCanvas = new Canvas();

    public static byte[] drawableToByte(Drawable drawable) {
        Bitmap bitmap;
        byte[] size = null;
        if (drawable instanceof BitmapDrawable){
            bitmap =((BitmapDrawable) drawable).getBitmap();
        } else {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();

            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap(w, h, config);
            final Canvas canvas = mCanvas;
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        size = baos.toByteArray();
        return size;
    }

    public static PackageItem getPackageItemFromPackageName(String packageName,
            PackageManager packageManager,boolean isPlayerApp) {
        PackageItem item = null;
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(packageName, 0);
            if (info == null) {
                Log.d(TAG, "install apk packinfo is null");
                return null;
            }
            //     String className = info.applicationInfo.className;
            String className = getLauncherClassByPackageName(packageName,packageManager);
            String appName = (String) info.applicationInfo
                    .loadLabel(packageManager);

            int flag = info.applicationInfo.flags;
            // IconData data = new IconData(drawableToByte(info.applicationInfo
            // .loadIcon(packageManager)));
            // item = new PackageItem(packageName, appName, className, data, flag);
            item = new PackageItem(packageName, appName, className, flag,isPlayerApp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static Drawable getIconDrawableFromPackageName(String packageName,
            PackageManager manager) {
        Drawable drawable = null;
    //    PackageInfo info = null;
        try {
           // info = manager.getPackageInfo(packageName, 0);
            drawable = manager.getApplicationIcon(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (info == null) {
//            Log.d(TAG, "install apk packinfo is null");
//            return null;
//        }
//        drawable = info.applicationInfo.loadIcon(manager);
        return drawable;

    }

    private static String getLauncherClassByPackageName(String pkg, PackageManager manager) {
        String className = null;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
        resolveIntent.setPackage(pkg);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveinfoList = manager
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            className = resolveinfo.activityInfo.name;
        }
        return className;
    }

    /**
     * apk安装、卸载时需要使用的对象
     * @return 安装、卸载具体实现方法的对象
     */
    public static Object getPackageManagerService(){
        Object packageManagerService = null;
        try {
            //通过ActivityThread获取PackageManagerService对象
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            //getPackageManager的参数
            Class<?>[] paramTypes = getParamTypes(activityThread,"getPackageManager");
            //获取getPackageManager的对象
            Method getPackageManager = activityThread.getMethod("getPackageManager", paramTypes);
            packageManagerService = getPackageManager.invoke(activityThread);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packageManagerService;
    }
    /**
     * 获取某个方法的参数
     * @param cls
     * @param mName 方法名
     * @return 参数类型的数组
     */
    public static Class<?>[] getParamTypes(Class<?> cls, String mName) {
        Class<?> cs[] = null;

        Method[] mtd = cls.getMethods();

        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) {
                continue;
            }
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }

}
