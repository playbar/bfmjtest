package com.baofeng.mj.business.videoplayer.Data;

import android.text.TextUtils;

import com.baofeng.mj.bean.PanoramaVideoAttrs;
import com.baofeng.mj.bean.PanoramaVideoBean;
import com.baofeng.mj.bean.VideoDetailBean;
import com.baofeng.mj.business.videoplayer.vrSurface.VrModel;
import com.baofeng.mj.unity.UnityPublicBusiness;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liuyunlong on 2016/6/28.
 */
public class VideoDetailInfo {


    public static boolean Suport4K = true;

    public static final String LowQuality = "240";
    public static final String NormalQuality = "480";
    public static final String HighQuality = "720";
    public static final String SuperQuality = "1080";
    public static final String Quality4K = "4k";


    public int Id;          //剧集id
    public String Name;     //名称
    public int TotalSet;    //总共多少集
    public String hpic;
    public VrModel.ScreenType video3DType;//视频3d类型
    public Map<String, AlbumData> Albums;
    public VrModel.ModelType modelType = VrModel.ModelType.MODEL_RECT;
    //用于报数
    public String resId;
    public int typeId;

    public static class AlbumData {
        public int AlbumId;
        public String HdType;//清晰度
        public int SetCount;//当前清晰度下多少集
        public Map<Integer, VideosData> VideoDatas;
    }

    public static class VideosData {
        public int Id;
        public int Seq;//第几集
        public String PlayUrl;
        public String Title;
        public int Start;//片头 片尾
        public int End;
    }

    public static VideoDetailInfo CreatFromPanoramaBean(PanoramaVideoBean data) {

        Suport4K = UnityPublicBusiness.getHigh();
        //Suport4K = value == 2;

        VideoDetailInfo detail = new VideoDetailInfo();
        detail.modelType = VrModel.ModelType.MODEL_SPHERE;
        detail.Id = Integer.parseInt((TextUtils.isEmpty(data.getRes_id()) ? "0" : data.getRes_id()));
        detail.Name = data.getTitle();
        detail.TotalSet = 0;
        //报数
        detail.typeId=data.getType();

        if (data.getThumb_pic_url() != null && data.getThumb_pic_url().size() > 0)
            detail.hpic = data.getThumb_pic_url().get(0);

        int v3DType = data.getVideo_dimension();
        int isPanorama = data.getIs_panorama();
        if (v3DType == 3) {
            detail.video3DType = VrModel.ScreenType.TYPE_LR3D;
        } else if (v3DType == 2) {
            detail.video3DType = VrModel.ScreenType.TYPE_UD3D;
        } else if (v3DType == 1 && isPanorama == 4) {
            detail.modelType = VrModel.ModelType.MODEL_BOX;
            detail.video3DType = VrModel.ScreenType.TYPE_2D;
        } else {
            detail.video3DType = VrModel.ScreenType.TYPE_2D;
        }

        //detail.video3DType = (Video3DType)Convert.ToInt32((data.video_dimension));
        detail.Albums = new HashMap<String, AlbumData>();
        if (data.getVideo_attrs() != null) {
            for (int i = 0; i < data.getVideo_attrs().size(); i++) {
                PanoramaVideoAttrs dAlbum = data.getVideo_attrs().get(i);

                String url = dAlbum.getPlay_url();
                if (url == null || url.equals("")) {
                    continue;
                }

                String hdType = dAlbum.getDefinition_name();
                if(!Suport4K && hdType.equals("4k"))
                    continue;

                AlbumData album = new AlbumData();
                album.HdType = hdType;
                album.VideoDatas = new HashMap<Integer, VideosData>();

                VideosData video = new VideosData();
                video.PlayUrl = url;
                //默认为第一集
                video.Seq = 1;
                album.VideoDatas.put(video.Seq, video);
                detail.Albums.put(album.HdType, album);
            }
        }
        detail.CleanAlbumData();
        return detail;
    }

    public static VideoDetailInfo CreatFromVideoDetailBean(VideoDetailBean data) {

        Suport4K = UnityPublicBusiness.getHigh();
        //Suport4K = value == 2;

        VideoDetailInfo detail = new VideoDetailInfo();
        detail.modelType = VrModel.ModelType.MODEL_RECT;
        detail.Id = data.getId();
        detail.Name = data.getTitle();
        detail.TotalSet = Integer.parseInt(data.getTotal());
        detail.hpic = data.getHpic();
        //用于报数
        detail.resId=String.valueOf(data.getId());
        detail.typeId=data.getCategory_type();

        int v3DType = data.getIs_3d();
        detail.video3DType = VrModel.ScreenType.values()[v3DType];
        detail.Albums = new HashMap<String, AlbumData>();
        for (int i = 0; i < data.getAlbums().size(); i++) {
            VideoDetailBean.AlbumsBean dAlbum = data.getAlbums().get(i);

            if(dAlbum.getHdtype() == 240)
                continue;

            AlbumData album = new AlbumData();
            album.AlbumId = dAlbum.getAlbumid();
            album.HdType = dAlbum.getHdtype() + "";
            album.SetCount = dAlbum.getMaxseq();
            album.VideoDatas = new HashMap<Integer, VideosData>();
            for (int v = 0; v < dAlbum.getVideos().size(); v++) {
                VideoDetailBean.AlbumsBean.VideosBean info = dAlbum.getVideos().get(v);
                String pUrl = info.getPlay_url();
                if (pUrl == null || pUrl.equals("")) {
                    continue;
                }

                VideosData video = new VideosData();
                video.Id = info.getVid();
                video.Seq = info.getSeq();
                video.Title = info.getTitle();

                video.PlayUrl = pUrl;
                video.Start = info.getStart();
                video.End = info.getEnd();
                album.VideoDatas.put(video.Seq, video);
            }
            detail.Albums.put(album.HdType, album);
        }

        detail.CleanAlbumData();
        return detail;
    }

    public void CleanAlbumData() {
        AlbumData lowQualityAlbum = Albums.get(LowQuality);
        AlbumData normalQualityAlbum = Albums.get(NormalQuality);
        AlbumData highQualityAlbum = Albums.get(HighQuality);
        AlbumData superQualityAlbum = Albums.get(SuperQuality);
        AlbumData Quality4kAlbum = Albums.get(Quality4K);

        Iterator<Map.Entry<String, AlbumData>> it = Albums.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, AlbumData> pairs = (Map.Entry) it.next();
            AlbumData album = pairs.getValue();
            for (int j = 1; j <= TotalSet; j++) {
                if (!album.VideoDatas.containsKey(j)) {
                    VideosData video = null;
                    if (highQualityAlbum != null) {
                        video = highQualityAlbum.VideoDatas.get(j);
                    }
                    if (superQualityAlbum != null && video == null) {
                        video = superQualityAlbum.VideoDatas.get(j);
                    }
                    if (normalQualityAlbum != null && video == null) {
                        video = normalQualityAlbum.VideoDatas.get(j);
                    }
                    if (lowQualityAlbum != null && video == null) {
                        video = lowQualityAlbum.VideoDatas.get(j);
                    }
                    if (video != null)
                        album.VideoDatas.put(j, video);
                }
            }
        }
    }
}
