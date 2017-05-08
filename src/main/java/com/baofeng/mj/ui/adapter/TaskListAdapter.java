package com.baofeng.mj.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.TaskBean;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.ApkUtil;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.NetworkUtil;
import com.baofeng.mj.util.viewutil.LanguageValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyang on 2016/5/11.
 */
public class TaskListAdapter extends BaseAdapter {
    private Context mContext;
    private List<TaskBean> mList = new ArrayList<TaskBean>();

    public TaskListAdapter(Context context, List<TaskBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mList == null || mList.isEmpty()) ? 0 : mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.task_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.task_img = (ImageView) view.findViewById(R.id.task_img);
            viewHolder.task_name = (TextView) view.findViewById(R.id.task_name);
            viewHolder.task_time = (TextView) view.findViewById(R.id.task_time);
            viewHolder.task_btn = (TextView) view.findViewById(R.id.task_btn);
            viewHolder.task_line = (View) view.findViewById(R.id.task_line);
            viewHolder.task_no = (TextView) view.findViewById(R.id.task_no);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        fillData(viewHolder, (TaskBean) getItem(position), position);
        return view;
    }

    /**
     * 填充数据
     *
     * @param viewHolder
     * @param data
     */
    public void fillData(final ViewHolder viewHolder, final TaskBean data, int position) {
        viewHolder.task_name.setText(data.getActivity_name());
        String startDate = data.getStart_date();
        if (!TextUtils.isEmpty(startDate)) {
            int datelong = Integer.parseInt(startDate);
            startDate = Common.getFormatDate(datelong * 1000L, "yyyy.MM.dd");
        }
        String endDate = data.getEnd_date();
        if (!TextUtils.isEmpty(endDate)) {
            int datelong = Integer.parseInt(endDate);
            endDate = Common.getFormatDate(datelong * 1000L, "yyyy.MM.dd");
        }
        if (position == 1) {
            viewHolder.task_line.setVisibility(View.GONE);
        } else {
            viewHolder.task_line.setVisibility(View.VISIBLE);
        }
        viewHolder.task_time.setText(LanguageValue.getInstance().getValue(mContext, "SID_EXPIRE") + startDate + "-" + endDate);
        if (data.getHadget() == 1) {
            viewHolder.task_btn.setVisibility(View.GONE);
            viewHolder.task_no.setVisibility(View.VISIBLE);
            viewHolder.task_no.setText("+" + data.getModou() + LanguageValue.getInstance().getValue(mContext, "SID_MODOU"));
        } else {
            viewHolder.task_btn.setVisibility(View.VISIBLE);
            viewHolder.task_no.setVisibility(View.GONE);
            viewHolder.task_btn.setTextColor(mContext.getResources().getColor(R.color.theme_main_color));
            viewHolder.task_btn.setBackground(mContext.getResources().getDrawable(R.drawable.corner_purple_bg));
            viewHolder.task_btn.setText(LanguageValue.getInstance().getValue(mContext, "SID_RECEIVE"));
            viewHolder.task_btn.setClickable(true);
            viewHolder.task_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkUtil.networkEnable()) {
                        String uid = UserSpBusiness.getInstance().getUid();
                        String versionCode = ApkUtil.getVersionNameSuffix();
                        getTask(mContext, uid, "mjapk", versionCode, data.getAid(), viewHolder.task_btn, viewHolder.task_no);
                    } else {
                        Toast.makeText(mContext, "网络断开连接！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 领取任务
     *
     * @param context
     * @param uid      用户id
     * @param from     获取途径
     * @param version  版本号
     * @param aid      任务id
     * @param task_btn 领取按钮
     */
    private void getTask(Context context, String uid, String from, String version, String aid, final TextView task_btn, final TextView task_no) {
        new UserInfoApi().getTask(context, uid, from, version, aid, new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(mContext, "领取失败", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.getBoolean("status")) {
                            updateView(task_btn, task_no, json.getLong("modou"));
                        } else {
                            Toast.makeText(mContext, "领取失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(mContext, "领取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新view并同步魔豆数量
     *
     * @param task_btn
     * @param modou
     */
    private void updateView(final TextView task_btn, final TextView task_no, final long modou) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                task_btn.setVisibility(View.GONE);
                task_no.setVisibility(View.VISIBLE);
                task_no.setText("+" + modou + LanguageValue.getInstance().getValue(mContext, "SID_MODOU"));
            }
        });
        new UserInfoApi().updateModouCount(mContext, new ApiCallBack<String>() {
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                try {
                    JSONObject json = new JSONObject(new String(result));
                    if (json.getBoolean("status")) {
                        JSONObject joData = json.getJSONObject("data");
                        String recharge_modou = joData
                                .getString("recharge_modou");
                        String gift_modou = joData.getString("gift_modou");
                        UserSpBusiness.getInstance().updateModouCount(recharge_modou, gift_modou);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ViewHolder {
        private ImageView task_img;
        private TextView task_name, task_time, task_btn, task_no;
        private View task_line;
    }
}
