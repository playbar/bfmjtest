package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.RechargeBean;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.publicutil.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaominglei on 2016/5/23.
 */
public class RechargeRecordAdapter extends BaseAdapter {
    private Context context;

    private List<RechargeBean> datas = new ArrayList<RechargeBean>();

    public RechargeRecordAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(List<RechargeBean> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (datas != null)
            return datas.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (datas != null)
            return datas.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (datas != null)
            return position;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.recharge_record_item_view, null);
            holder = new ViewHolder();
            holder.tv_mobi_count = (TextView) convertView.findViewById(R.id.tv_mobi_count);
            holder.recoderTime = (TextView) convertView.findViewById(R.id.charge_recoder_time);
            holder.recoderNum = (TextView) convertView.findViewById(R.id.charge_recoder_num);
            holder.recoderMobile = (TextView) convertView.findViewById(R.id.charge_recoder_mobile);
            holder.recoderStatus = (TextView) convertView.findViewById(R.id.charge_recoder_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RechargeBean model = datas.get(position);
        setModel(holder, model);
        return convertView;
    }

    /**
     * @author wanghongfang  @Date 2015-2-2 下午3:07:07
     * description:设置数据更新页面
     * @param model 充值数据
     */
    private void setModel(ViewHolder hodler, RechargeBean model) {
        if (model == null)
            return;
        String sMoCoinFormat = BaseApplication.INSTANCE.getResources().getString(R.string.recharge_record_mobi);
        String finalMoCoin = String.format(sMoCoinFormat, model.getReceiving_modou(), model.getRecharge_money());
        hodler.tv_mobi_count.setText(finalMoCoin);
        int status = model.getOrder_status();
        hodler.recoderStatus.setText(status == 3 ? R.string.paid
                : R.string.unpaid);

        if (model.getRecharge_time() != 0) {
            String timestr = DateUtil.date2String(model.getRecharge_time() * 1000L, "yyyy-MM-dd HH:mm:ss");
            hodler.recoderTime.setText(timestr);
        }
        hodler.recoderMobile.setText(model.getRecharge_phone());
        hodler.recoderNum.setText(model.getOrder_number());
    }

    class ViewHolder {
        TextView tv_mobi_count;

        protected TextView recoderTime;

        protected TextView recoderMobile;

        protected TextView recoderNum;

        protected TextView recoderStatus;
    }
}
