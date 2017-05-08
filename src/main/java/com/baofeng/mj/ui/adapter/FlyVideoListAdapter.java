package com.baofeng.mj.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.ReportClickBean;
import com.baofeng.mj.bean.Resource.PageItem;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.business.localbusiness.flyscreen.util.FlyScreenUtil;
import com.baofeng.mj.business.publicbusiness.ReportBusiness;
import com.baofeng.mj.ui.activity.MediaGlActivity;
import com.baofeng.mj.ui.fragment.FlyScreenFragment;
import com.baofeng.mj.util.publicutil.GlideUtil;
import com.baofeng.mj.util.viewutil.ShowUi;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 飞屏详细列表
 */
public class FlyVideoListAdapter extends NormalBaseAdapter<PageItem> {
    private FlyScreenFragment fragment;
    private Context mContext;

    public FlyVideoListAdapter(List<PageItem> datas, Context context,FlyScreenFragment fragment,
                               int LayoutId) {
        super(datas, context, LayoutId);
        this.mContext = context;
        this.fragment = fragment;
    }

    @Override
    protected Object initViewHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.fileImage = new WeakReference<ImageView>((ImageView) convertView
                .findViewById(R.id.iv_file_image));
        viewHolder.fileName = (TextView) convertView
                .findViewById(R.id.tv_file_name);
        viewHolder.videoPlay = (TextView) convertView
                .findViewById(R.id.tv_video_play);
        return viewHolder;
    }

    @Override
    protected void handleViewholder(Object viewholder, int pos, View convertView) {
        ViewHolder v = (ViewHolder) viewholder;
        PageItem pageItem = getData(pos);
        final String videoName = stringFilter(pageItem.getName());
//        v.fileName.setText(videoName);
        ShowUi.showFlyScreenTitle(v.fileName, videoName, 12);//显示文件名
        if (pageItem.getBDir()) {
//            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.res_fly_screen_fragment_file_img,
//                    v.fileImage, ImageLoaderUtils.getInstance().getImageOptionsFlyScreen());
            GlideUtil.displayImage(fragment, v.fileImage, R.drawable.res_fly_screen_fragment_file_img, R.drawable.default_fly_screen_video);
            //v.fileImage.get().setTag("");
            v.videoPlay.setVisibility(View.GONE);
            int width = v.fileImage.get().getLayoutParams().width;
            v.fileImage.get().getLayoutParams().height = width ;
        } else {
//            v.videoPlay.setVisibility(View.VISIBLE);
//            final String path = FlyScreenUtil.getResUri(
//                    FlyScreenUtil.urlEncode(pageItem.getUri()), FlyScreenBusiness.getInstance().getCurrentDevice());
//
//           final boolean hasSub = pageItem.getSubtitleType()>0 ?true:false; //是否有字幕文件
//
//            v.videoPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    reportClick("preview");
//                    Intent intent = new Intent(mContext, MediaGlActivity.class);
//                    intent.putExtra("videoPath", path);
//                    intent.putExtra("videoName", videoName);
//                    intent.putExtra("hasSub",hasSub);
//                    mContext.startActivity(intent);
//                }
//            });
            GlideUtil.displayImage(fragment, v.fileImage, R.drawable.default_fly_screen_video, R.drawable.default_fly_screen_video);
        }
    }

    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    private class ViewHolder {
        WeakReference<ImageView> fileImage;
        TextView fileName;
        TextView videoPlay;
    }


    //click 报数
    private void reportClick(String airvideohelp){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setClicktype("chooseitem");
        bean.setTpos("1");
        bean.setPagetype("airvideo");
        bean.setLocal_menu_id("3");
        bean.setAirevideohelp(airvideohelp);
        ReportBusiness.getInstance().reportClick(bean);
    }
}
