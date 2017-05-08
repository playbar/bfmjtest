package com.baofeng.mojing.sdk.login.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class SdkUtils {
	public static Intent getExplicitIntent(Context context, Intent implicitIntent, String currPackageName) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() == 0) {
            return null;
        }

        int index = 0;
        if (currPackageName != null && !currPackageName.equals("")) {
	    	for (int i = 0; i < resolveInfo.size(); i++) {
	    		 ResolveInfo serviceInfo = resolveInfo.get(i);
	    	     String packageName = serviceInfo.serviceInfo.packageName;
	    	     if (packageName.equals(currPackageName)) {
	    	    	 index = i;
	    	    	 break;
	    	     }
			}
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(index);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        //Log.d("test2", packageName +"----"+ className);
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        
        return explicitIntent;
    }
}
