package com.baofeng.mj.bean;

import java.io.Serializable;

/**
 * 本地已安装apk实体类
 */
public class ApkInstallInfo implements Serializable{
	private static final long serialVersionUID = 7932693667305373397L;
	private String appGameId;
	private int versionCode;
	private String packageName;
	private String appName;
	private String appIcoUrl = "";
	private String icon_url = "";
	private int isSelf = 1;

	private String mainAngle = "0";
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public void setAppName(String name){
		this.appName = name;
	}
	public String getAppName(){
		return this.appName;
	}
	public void setAppIcoUrl(String icoUrl) {
		appIcoUrl = icoUrl;
	}
	public String getAppIcoUrl() {
		return appIcoUrl;
	}
	public void setAppGameId(String gameId) {
		appGameId = gameId;
	}
	public String getAppGameId() {
		return appGameId;
	}

	public void setIconUrl(String url){
		this.icon_url = url;
	}

	public String getIconUrl(){
		return this.icon_url;
	}
	public int getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(int isSelf) {
		this.isSelf = isSelf;
	}

	public String getMainAngle() {
		return mainAngle;
	}

	public void setMainAngle(String mainAngle) {
		this.mainAngle = mainAngle;
	}
}
