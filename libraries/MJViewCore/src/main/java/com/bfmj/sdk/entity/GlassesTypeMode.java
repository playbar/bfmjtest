package com.bfmj.sdk.entity;

import java.io.Serializable;

public class GlassesTypeMode  implements Serializable{
	/**
	 * 眼镜-镜片管理数据类型
	 */
	private static final long serialVersionUID = 1L;
	private String ClassName;
	private String ReleaseDate;
	private String Display;
	private String URL;
	private String KEY;
	private String ID;
	
	public GlassesTypeMode(String className, String releaseDate,
			String display, String uRL, String kEY,String id) {
		super();
		ClassName = className;
		ReleaseDate = releaseDate;
		Display = display;
		URL = uRL;
		KEY = kEY;
		ID = id;
	}
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	public String getReleaseDate() {
		return ReleaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		ReleaseDate = releaseDate;
	}
	public String getDisplay() {
		return Display;
	}
	public void setDisplay(String display) {
		Display = display;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}

	 
}
