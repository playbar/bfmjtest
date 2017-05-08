package com.baofeng.mj.business.publicbusiness;

/**
 * 响应码配置
 */
public class ResponseCodeUtil {
	public static final String SUCCESS = "100000";// 操作成功
	public static final String NET_ERROR = "100001";// 系统繁忙，请稍后再试
	public static final String AUTH_FAILED = "000002";// 接口认证失败
	public static final String NOT_EXIST = "000004";// 接口不存在
	public static final String PARAMS_NULL = "000003";// 参数不能为空
	public static final String PARAMS_ERROR = "000005";// 参数错误
	public static final String USER_EXIST = "000006";// 用户已存在
	public static final String RES_HAS_BUY = "000007";// 资源已订购
	public static final String RES_NOT_BUY = "000008";// 资源未订购
	public static final String RES_NOT_EXIST = "000009";// 资源不存在
	public static final String MODOU_NOT_ENOUGH = "001001";// 魔豆数量不足
	public static final String MOBI_NOT_ENOUGH = "001004";// 魔币数量不足
	public static final String GIFT_HAS_USED = "001002";// 礼券已使用
	public static final String BOUND_FAILED = "001003";// 绑定失败
	public static final String NOT_RESOULT = "100030";// 查询无结果
	public static final String PIC_CONFIG_ERROR = "200500";// 图片格式配置参数错误
	public static final String PIC_PARAMS_ERROR = "200501";// 参数错误，请传递图片类型参数
	public static final String UPLOAD_FAILED = "200505"	;//	上传失败，请稍候再试

	/**
	 * 获取失败原因
	 */
	public static String getFailureReason(String code){
		if(MODOU_NOT_ENOUGH.equals(code)){// 魔豆数量不足
			return "您的魔豆余额不足，购买失败";
		}else if(MOBI_NOT_ENOUGH.equals(code)) {// 魔币数量不足
			return "您的魔币余额不足，购买失败";
		}else if(RES_HAS_BUY.equals(code)) {// 资源已订购
			return "资源已经购买过，购买失败";
		}else if(PARAMS_ERROR.equals(code)) {// 参数错误
			return "参数错误，购买失败";
		}else if(PARAMS_NULL.equals(code)) {// 参数不能为空
			return "参数不能为空，购买失败";
		}else{
			return "网络不太给力，购买失败";
		}
	}

	/**
	 * 魔豆不足
	 * @return true不足，false充足
	 */
	public static boolean modouNotEnough(String code){
		if(MODOU_NOT_ENOUGH.equals(code)) {
			return true;
		}
		return false;
	}

	/**
	 * 魔币不足
	 * @return true不足，false充足
	 */
	public static boolean mobiNotEnough(String code){
		if(MOBI_NOT_ENOUGH.equals(code)) {
			return true;
		}
		return false;
	}

	/**
	 * 魔豆或者魔币不足
	 * @return true不足，false充足
	 */
	public static boolean modouOrMobiNotEnough(String code){
		if(MODOU_NOT_ENOUGH.equals(code) || MOBI_NOT_ENOUGH.equals(code)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否购买过
	 * @return true已购买，false未购买
	 */
	public static boolean ifHasPayed(String code){
		if(RES_HAS_BUY.equals(code)) {// 资源已订购
			return true;
		}
		return false;
	}
}
