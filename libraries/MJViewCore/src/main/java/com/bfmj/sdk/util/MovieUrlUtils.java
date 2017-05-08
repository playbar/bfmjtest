package com.bfmj.sdk.util;

public class MovieUrlUtils {
	private int mType = 0;
	private static MovieUrlUtils mInstance;

	public final static String mDomain = "http://i.mojing.baofeng.com/v1.0/public/";
	
	public static MovieUrlUtils getInstance() {    
		if (mInstance == null) {
			mInstance = new MovieUrlUtils(); 
		}    
		
		return mInstance;    
	}
	
	/**
	 * 初始化数据
	 * @author linzanxian  @Date 2015年3月31日 上午11:21:26
	 * description:初始化数据
	 * @return void
	 */
	public void init() {
		mType = 0;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}


	/**
	 * 获取详情页地址
	 * @author linzanxian  @Date 2015年4月3日 上午9:43:35
	 * description:获取详情页地址
	 * @param movieid 影片ID
	 * @return String
	 */
	public String getDetailUrl(int movieid) {
		String detailUrl = mDomain +"movie/" + (movieid % 1000) + "/" + movieid + ".js";
		
		return detailUrl;
	}
}
