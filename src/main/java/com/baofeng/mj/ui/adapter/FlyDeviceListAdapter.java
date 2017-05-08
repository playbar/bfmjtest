package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.DeviceInfo;

import java.util.List;

/**
 * ClassName: FlyDeviceListAdapter <br/>
 *
 * @author qiguolong
 * @date: 2015-8-31 下午6:13:57 <br/>
 * @description:飞屏设备
 */
public class FlyDeviceListAdapter extends NormalBaseAdapter<DeviceInfo> {

    /**
     * @return {返回值说明}
     * @author qiguolong @Date 2015-8-31 下午6:39:47
     * description:{这里用一句话描述这个方法的作用}
     * @param {引入参数名  {引入参数说明}
     */
    public FlyDeviceListAdapter(List<DeviceInfo> datas, Context context,
                                int LayoutId) {
        super(datas, context, LayoutId);
    }

    @Override
    protected Object initViewHolder(View convertView) {
        viewHolder vHolder = new viewHolder();
        vHolder.name = (TextView) convertView.findViewById(R.id.device_name);
        return vHolder;
    }

    @Override
    protected void handleViewholder(Object viewholder, int pos, View convertView) {
        viewHolder v = (viewHolder) viewholder;
        DeviceInfo d = getData(pos);
        v.name.setText(d.getName());
    }

    class viewHolder {
        TextView name;
    }
}
