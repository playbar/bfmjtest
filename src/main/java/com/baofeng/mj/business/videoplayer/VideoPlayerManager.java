package com.baofeng.mj.business.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.videoplayer.vrSurface.VrModel;
import com.storm.smart.core.IProxy;
import com.storm.smart.core.URlHandleProxyFactory;
import com.storm.smart.domain.P2pInfo;
import com.storm.smart.play.baseplayer.BaseSurfacePlayer;
import com.storm.smart.play.call.IBaofengPlayer;
import com.storm.smart.play.call.IBfPlayerConstant;
import com.storm.smart.play.call.PlayerWithoutSurfaceFactory;
import com.storm.smart.play.utils.PlayCheckUtil;


/**
 * Created by liuyunlong on 2016/6/22.
 */
public class VideoPlayerManager {

    final String TAG = "VideoPlayerManager";

    public static int MSG_SURFACE_CHANGE = 0;
    public static int MSG_LOADING = MSG_SURFACE_CHANGE + 1;
    public static int MSG_LOADING_END = MSG_LOADING + 1;
    public static int MSG_PLAY_START = MSG_LOADING_END + 1;
    public static int MSG_PLAY_END = MSG_PLAY_START + 1;
    public static int MSG_PLAY_ERROR = MSG_PLAY_END + 1;
    public static int MSG_SUBTITLE_INIT = MSG_PLAY_ERROR + 1;


    public static int PLAYER_SOFT = IBfPlayerConstant.IBasePlayerType.TYPE_SOFT;
    public static int PLAYER_SYS = IBfPlayerConstant.IBasePlayerType.TYPE_SYS;
    public static int PLAYER_SYSPLUS = IBfPlayerConstant.IBasePlayerType.TYPE_SYSPLUS;

    private Handler m_handler = null;

    VideoSurface m_surface;
    Context m_context;
    VrModel.ScreenType m_3dType;
    String m_curPath;
    BaseSurfacePlayer player;
    public boolean m_localPause = false;
    IProxy proxy;
   public int m_decodeType;
    String m_playPath;
    int m_startPos;

    VrModel.ModelType m_modeleType = VrModel.ModelType.MODEL_RECT;

    public  VideoPlayerManager(Context context)
   {
       m_localPause = false;
       m_context = context;
   }

 /*   public View getSurfaceView(){
        return MojingSDKHandler.getInstance().getView();
    }*/

    public void setHandler(Handler h)
    {
        m_handler = h;
    }

    public void set3DType(VrModel.ScreenType type)
    {
        m_3dType = type;



        if(m_surface != null)
            m_surface.set3DType(m_3dType);
    }
    public void setModelType(VrModel.ModelType type)
    {
        m_modeleType = type;
        if(m_surface != null)
            m_surface.initSurface(m_3dType,m_modeleType);
        if(type == VrModel.ModelType.MODEL_SPHERE||type== VrModel.ModelType.MODEL_SPHERE180){
            setInitAngle(90);
        }
    }

    public void setInitAngle(int angle){
        if(m_surface!=null){
            m_surface.setInitAngle(angle);
        }
    }


    void InitPlayer(){
        PlayCheckUtil.setSupportLeftEye(true);
        stopPlayer();
        player = PlayerWithoutSurfaceFactory.createPlayer((Activity) m_context, m_decodeType,false);
        player.setListener(playerListener);
    }
    public void swithcPlayer(int playerType)
    {
        int pos = getCurPos();
        m_decodeType = playerType;
        loadMovie(m_curPath,m_3dType,m_decodeType,pos);
    }
    public void loadMovie(String path, VrModel.ScreenType video3dType, int decodeType, int startPos)
    {
        stopPlayer();
        stopP2P();
        m_curPath = path;
        m_decodeType = decodeType;
        m_startPos = startPos;
        set3DType(video3dType);

        int pid;
        if (path.startsWith("qstp:")) {
            pid =  URlHandleProxyFactory.getIProxy(BaseApplication.INSTANCE, path);
            proxy = URlHandleProxyFactory.getInstance();
            StartPlay(P2pInfo.P2P_PLAY_SERVER_PATH);
        }
        else if(path.startsWith("yun:")||path.startsWith("yunlive:"))
        {
            URlHandleProxyFactory.getIProxy(BaseApplication.INSTANCE, path);
            proxy = URlHandleProxyFactory.getInstance();
            proxy.p2pStartPlay(path);
            proxy.setcallback(new IProxy.UrlCallBack() {
                @Override
                public void mcallBack(String state) {
                   String playPath = m_curPath;
                    if (state != null && !"4".equals(state)) {
                        playPath = state;
                    }
                    StartPlay(playPath);
                }
            });
        }
        else
        {
            StartPlay(m_curPath);
        }

    }

    void StartPlay(String path)
    {
        InitPlayer();
        bindSurface();
        if (!player.setVideoPath(path)) {
        }
    }

    void bindSurface(){
        if(player == null)
            return;
        if(m_surface == null) {
            createSurface();
        }
        else
        {
            m_surface.initSurface(m_3dType, m_modeleType); //m_modeleType

            if(m_surface.getSurfaceTexture() != null && player.getmSurfaceTexture() == null)
            {
                player.setSurfaceTexture(m_surface.getSurfaceTexture());
                player.setSurface(new Surface(m_surface.getSurfaceTexture()));
               // player.setSurface( m_surface.getSurface());
            }

        }

        //MojingSDKHandler.getInstance().setRender(m_surface);
    }


    void createSurface()
    {
        if(m_surface == null) {
            m_surface = new VRVideoSurface(m_context);
            m_surface.initSurface(m_3dType, m_modeleType); //m_modeleType

            Handler handler = new Handler(){
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            if(player != null && player.getmSurfaceTexture() == null)
                            {
                                player.setSurfaceTexture(m_surface.getSurfaceTexture());
                                player.setSurface( new Surface(m_surface.getSurfaceTexture()));
                            }
                            break;
                    }
                    super.handleMessage(msg);
                }
            };
            m_surface.setHandler(handler);
        }

    }

    public VideoSurface getSurface()
    {
        if(m_surface == null)
            createSurface();

            return m_surface;
    }


    public void playPause()
    {
        if(player != null)
            player.pause();
        m_localPause = true;
    }
    public void playContinue()
    {
        m_localPause = false;
        if(player != null)
            player.start();
    }
    public void playSeek(int pos)
    {
        if(player != null)
            player.seekTo(pos);
    }
    public void Quite(boolean finish)
    {
        stopPlayer();
        stopP2P();
    }
    public void relseSurface(){
        if(m_surface != null)
            m_surface.Release();
    }

    void stopP2P()
    {
        if(proxy!=null){
            proxy.p2pStopPlay();
            proxy.p2pUninit();
            proxy = null;
        }
    }

    void stopPlayer()
    {
        if(player != null)
        {
            player.stop();
            player.release();
            player = null;
        }
    }

    public  int getSpeed()
    {
        int speed = 0;
        if(proxy!=null){
            speed = proxy.p2pGetSpeed(0);
        }
        return speed;
    }

    public int getPlayerType()
    {
        return m_decodeType;
    }
    public int getCurPos()
    {
        int pos = 0;
        if(player != null)
            pos =  player.getCurrentPosition();
        return pos;
    }
    public int getDuration()
    {
        int duration = 1;
        if(player != null)
            duration = player.getDuration();
        return duration;
    }

    public boolean isPlaying()
    {
        return !m_localPause;
    }
    public boolean isStarted()
    {
        return  player != null && player.isPlayerPrepared();
    }


    void onPlayStart()
    {
        int videoWidht = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        m_surface.setVideoSize(videoWidht,videoHeight);

        playSeek(m_startPos);
        if(m_handler != null)
        {
            Message msg = new Message();
            msg.what = MSG_PLAY_START;
            m_handler.sendMessage(msg);
        }

        if(m_localPause)
            playPause();
    }
    void onPlayFinish()
    {
        if(m_handler != null) {
            Message msg = new Message();
            msg.what = MSG_PLAY_END;
            m_handler.sendMessage(msg);
        }
    }
    void onPlayError(int code)
    {
        if (code == -38 || code == 100000 ) //mediaplayer state error ..android bug
            return;

        if(m_decodeType == PLAYER_SYS)
            swithcPlayer(PLAYER_SYSPLUS);
        else if(m_decodeType != PLAYER_SOFT)
            swithcPlayer(PLAYER_SOFT);
        else
        {
            if(m_handler != null) {
                Message msg = new Message();
                msg.what = MSG_PLAY_ERROR;
                m_handler.sendMessage(msg);
            }
        }
    }
    void onInitSubtitle()
    {
        if(m_handler != null) {
            Message msg = new Message();
            msg.what = MSG_SUBTITLE_INIT;
            m_handler.sendMessage(msg);
        }
    }
    void showLoadingTips(boolean show)
    {
        if(m_handler != null) {
            Message msg = new Message();
            msg.what = show ? MSG_LOADING : MSG_LOADING_END;
            m_handler.sendMessage(msg);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //listener
    private BFPlayerListener playerListener = new BFPlayerListener() {
        @Override
        public void onPrepared(IBaofengPlayer bfPlayer) {

//            render.setVideoSize(bfPlayer.getVideoWidth(), bfPlayer.getVideoHeight());
            bfPlayer.start();

            onPlayStart();
        }

        @Override
        public void onCompletion(IBaofengPlayer bfPlayer) {
            onPlayFinish();
        }

        @Override
        public void onError(IBaofengPlayer bfPlayer, int what) {
            onPlayError(what);
        }

        @Override
        public void onInfo(IBaofengPlayer bfPlayer, int what, final Object extra) {
            switch (what)
            {
                case 701:
                case 703:
                    showLoadingTips(true);

                    break;
                case 702:
                case 704:
                    showLoadingTips(false);
                    break;
                case 1023:
                    onInitSubtitle();
                    break;
            }
        }

        @Override
        public void onSeekToComplete(IBaofengPlayer bfPlayer) {
            if (!m_localPause && !player.isPlaying())
                player.start();
        }

        @Override
        public boolean onSwitchPlayer(IBaofengPlayer bfPlayer, Object item, int playTime) {
            return false;
        }

        @Override
        public void onRawVideoDataUpdate() {

        }

        @Override
        public void onVideoInfo(int y, int u, int v, int stride, int width, int height, int
                aspect) {

        }
    };
}
