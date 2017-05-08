package com.bfmj.sdk.util;

import com.bfmj.viewcore.render.GLScreenParams;

/**
 * 公共操作类
 * ClassName: MovieCommonUtils <br/>
 * @author linzanxian    
 * @date: 2015年4月1日 上午11:33:07 <br/>  
 * description:公共操作类
 */
public class MovieCommonUtils {
	
	/**
	 * 获取直实大小
	 * @author linzanxian  @Date 2015年4月1日 上午11:34:23
	 * description:获取直实大小
	 * @param size 坐标大小 
	 * @return float
	 */
	public static float getUnit(float size) {
		return size * GLScreenParams.UNIT;
	}
	
	/**
	 * 获取集数
	 * @author linzanxian  @Date 2015年4月7日 上午11:50:02
	 * description:获取集数
	 * @param seq 集数
	 * @param seq_no 分集数
	 * @return {返回值说明}
	 */
	public static String getNumber(int seq, int seq_no) {
		String unit = "";
		switch (seq_no) {
			case 1:
				unit = "A";
				break;
				
			case 2:
				unit = "B";
				break;
				
			case 3:
				unit = "C";
				break;
				
			case 4:
				unit = "D";
				break;
				
			case 5:
				unit = "E";
				break;
	
			default:
				break;
		}
		
		return seq + unit;
	}
}
