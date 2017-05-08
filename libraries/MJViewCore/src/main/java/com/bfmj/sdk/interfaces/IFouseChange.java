package com.bfmj.sdk.interfaces;

/**
 * 焦点切换接口(想获取虚拟焦点以及按键控制的自定义组件都要实现该接口)
 * @author yanzw
 * @date 2014-9-9 下午5:19:40
 */
public interface IFouseChange {
	//获取焦点的回调
	public void onGainFocus();
	//失去焦点的回调
	public void onLostFocus();
	//向左切换焦点,返回true，代表处理了该事件，返回false代表组件内部不需要焦点了。由焦点总控制的BaseActivity切换到下一个可以获取焦点的组件
	public boolean toLeftFocus();
	//向右切换焦点
	public boolean toRightFocus();
	//向上切换焦点
	public boolean toUpFocus();
	//向下切换焦点
	public boolean toDownFocus();
	//确认焦点
	public void toCenterFocus();
	//返回键处理
	public boolean toBack();	
	//按键事件
	public boolean onKeyDown(int keyCode);
	//按键事件
	public boolean onKeyUp(int keyCode);
}
