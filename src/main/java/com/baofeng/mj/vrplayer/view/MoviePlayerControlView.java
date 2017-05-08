package com.baofeng.mj.vrplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baofeng.mj.R;
import com.baofeng.mj.vrplayer.activity.GLBaseActivity;
import com.baofeng.mj.vrplayer.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.vrplayer.interfaces.IViewVisiableListener;
import com.baofeng.mj.vrplayer.utils.BitmapUtil;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.baofeng.mj.vrplayer.utils.MJGLUtils;
import com.bfmj.sdk.util.TimeFormat;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;

/**
 * yushaochen 2017/4/6
 * 播放控制层
 */
public class MoviePlayerControlView extends GLRelativeView{

    public static final int MOVIE = 0;
    public static final int LOCAL_MOVIE = 1;
    public static final int PANO = 2;
    public static final int LOCAL_PANO = 3;

    private int mType = 0;

    public static final String SELECTED_SOURCE = "selected_source";
    public static final String SOUND = "sound";
    public static final String HD = "hd";
    public static final String SETTING = "setting";

    private Context mContext;
    private GLRelativeView mRootView;

    private float mWidth = 1000f;
    private float mHeigth = 200f;

    private GLSeekBarView seekBarView;

    private GLLinearView mBottomView;//
    private GLTextView mCurrentTime;//当前播放时间
    private GLTextView mTotalTime;//当前影片总时长
    private GLTextView mName;//影片名称

    private PlayControlButton mSelectedSourceBtn;//选集按钮
    private PlayControlButton mSoundBtn;//音量按钮
    private PlayControlButton mHDBtn;//清晰度按钮
    private PlayControlButton mSettingBtn;//设置按钮

    private GLImageView mPlayOrPauseBtn;//播放或者暂停按钮

    private boolean playFlag = false;//当前按钮显示暂停

    public MoviePlayerControlView(Context context) {
        super(context);
        mContext = context;
        setLayoutParams(mWidth,mHeigth);
        mRootView = new GLRelativeView(mContext);
        mRootView.setLayoutParams(mWidth,mHeigth);
        Bitmap bitmap = BitmapUtil.getBitmap((int) mWidth, (int) mHeigth, 20f, "#19191a");
        mRootView.setBackground(bitmap);
//        setBackground(new GLColor(0x00ff00));
        //创建进度条
        createSeekBarView();
        
        //创建底部显示
        createBottomView();

        //创建顶部控制按钮
        createTopView();

        addView(mRootView);

        mRootView.setFocusListener(focusListener);

        mRootView.setVisible(false);
    }

    private GLViewFocusListener focusListener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView view, boolean focused) {
            if(focused) {
                ((GLBaseActivity)getContext()).showCursorView();
                if(null != mCallBack) {
                    mCallBack.onHideControlAndSettingView(false);
                }
            } else {
                ((GLBaseActivity)getContext()).hideCursorView2();
                if(null != mCallBack) {
                    mCallBack.onHideControlAndSettingView(true);
                }
            }
        }
    };

    private void createTopView() {
        mSelectedSourceBtn = new PlayControlButton(mContext);
        mSelectedSourceBtn.setId(SELECTED_SOURCE);
        mSelectedSourceBtn.setMargin(40f,33f,0f,0f);
        mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
        mSelectedSourceBtn.setText("选集");
        mSelectedSourceBtn.setSelected(false);
        mSelectedSourceBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(!mSelectedSourceBtn.isSelected()) {
                    if(focused) {
                        mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_hover);
                        mSelectedSourceBtn.setTextVisible(true);
                    } else {
                        mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
                        mSelectedSourceBtn.setTextVisible(false);
                    }
                } else {
                    if(focused) {
                        mSelectedSourceBtn.setTextVisible(true);
                    } else {
                        mSelectedSourceBtn.setTextVisible(false);
                    }
                }
            }
        });
        mSelectedSourceBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                resetOtherButton(SELECTED_SOURCE);
                if(!mSelectedSourceBtn.isSelected()) {
                    mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_click);
                    mSelectedSourceBtn.setSelected(true);
                } else {
                    mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_hover);
                    mSelectedSourceBtn.setSelected(false);
                }
                if(null != mCallBack) {
                    mCallBack.onControlChanged(mSelectedSourceBtn.getId(),mSelectedSourceBtn.isSelected());
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        mRootView.addView(mSelectedSourceBtn);

        mSoundBtn = new PlayControlButton(mContext);
        mSoundBtn.setId(SOUND);
        mSoundBtn.setMargin(40f+44f+257f,33f,0f,0f);
        mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
        mSoundBtn.setText("音量");
        mSoundBtn.setSelected(false);
        mSoundBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(!mSoundBtn.isSelected()) {
                    if(focused) {
                        mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_hover);
                        mSoundBtn.setTextVisible(true);
                    } else {
                        mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
                        mSoundBtn.setTextVisible(false);
                    }
                } else {
                    if(focused) {
                        mSoundBtn.setTextVisible(true);
                    } else {
                        mSoundBtn.setTextVisible(false);
                    }
                }
            }
        });
        mSoundBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                resetOtherButton(SOUND);
                if(!mSoundBtn.isSelected()) {
                    mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_click);
                    mSoundBtn.setSelected(true);
                } else {
                    mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_hover);
                    mSoundBtn.setSelected(false);
                }
                if(null != mCallBack) {
                    mCallBack.onControlChanged(mSoundBtn.getId(),mSoundBtn.isSelected());
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        mRootView.addView(mSoundBtn);

        mHDBtn = new PlayControlButton(mContext);
        mHDBtn.setId(HD);
        mHDBtn.setMargin(40f+2*44f+257f+2*80f+70f,33f,0f,0f);
        mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
        mHDBtn.setText("画质");
        mHDBtn.setSelected(false);
        mHDBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(!mHDBtn.isSelected()) {
                    if(focused) {
                        mHDBtn.setImageBg(R.drawable.play_icon_function_definition_hover);
                        mHDBtn.setTextVisible(true);
                    } else {
                        mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
                        mHDBtn.setTextVisible(false);
                    }
                } else {
                    if(focused) {
                        mHDBtn.setTextVisible(true);
                    } else {
                        mHDBtn.setTextVisible(false);
                    }
                }
            }
        });
        mHDBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                resetOtherButton(HD);
                if(!mHDBtn.isSelected()) {
                    mHDBtn.setImageBg(R.drawable.play_icon_function_definition_click);
                    mHDBtn.setSelected(true);
                } else {
                    mHDBtn.setImageBg(R.drawable.play_icon_function_definition_hover);
                    mHDBtn.setSelected(false);
                }
                if(null != mCallBack) {
                    mCallBack.onControlChanged(mHDBtn.getId(),mHDBtn.isSelected());
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        mRootView.addView(mHDBtn);

        mSettingBtn = new PlayControlButton(mContext);
        mSettingBtn.setId(SETTING);
        mSettingBtn.setMargin(40f+3*44f+2*257f+2*80f+70f,33f,0f,0f);
        mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
        mSettingBtn.setText("设置");
        mSettingBtn.setSelected(false);
        mSettingBtn.setImageFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {

                if(!mSettingBtn.isSelected()) {
                    if(focused) {
                        mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_hover);
                        mSettingBtn.setTextVisible(true);
                    } else {
                        mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
                        mSettingBtn.setTextVisible(false);
                    }
                } else {
                    if(focused) {
                        mSettingBtn.setTextVisible(true);
                    } else {
                        mSettingBtn.setTextVisible(false);
                    }
                }
            }
        });
        mSettingBtn.setImageKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                resetOtherButton(SETTING);
                if(!mSettingBtn.isSelected()) {
                    mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_click);
                    mSettingBtn.setSelected(true);
                } else {
                    mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_hover);
                    mSettingBtn.setSelected(false);
                }
                if(null != mCallBack) {
                    mCallBack.onControlChanged(mSettingBtn.getId(),mSettingBtn.isSelected());
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        mRootView.addView(mSettingBtn);

        //创建播放按钮
        mPlayOrPauseBtn = new GLImageView(mContext);
        mPlayOrPauseBtn.setLayoutParams(70f,70f);
        mPlayOrPauseBtn.setMargin(40f+2*44f+257f+80f,20f,0f,0f);
        mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_pause_normal);
        mPlayOrPauseBtn.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(focused) {
                    if(playFlag) {//当前显示播放
                        mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_play_hover);
                    } else {//当前显示暂停
                        mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_pause_hover);
                    }
                } else {
                    if(playFlag) {//当前显示播放
                        mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_play_normal);
                    } else {//当前显示暂停
                        mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_pause_normal);
                    }
                }
            }
        });
        mPlayOrPauseBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(!playFlag) {
                    setPlayOrPauseBtn(true);
                } else {
                    setPlayOrPauseBtn(false);
                }
                if(null != mCallBack) {
                    mCallBack.onPlayChanged(playFlag);
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        HeadControlUtil.bindView(mPlayOrPauseBtn);
        mRootView.addView(mPlayOrPauseBtn);
    }

    /**
     * 点击时其它按钮置灰
     * @param id
     */
    private void resetOtherButton(String id) {
        if(SELECTED_SOURCE.equals(id)) {
            mSoundBtn.setSelected(false);
            mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
            mHDBtn.setSelected(false);
            mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
            mSettingBtn.setSelected(false);
            mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
        } else if(SOUND.equals(id)) {
            mSelectedSourceBtn.setSelected(false);
            mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
            mHDBtn.setSelected(false);
            mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
            mSettingBtn.setSelected(false);
            mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
        } else if(HD.equals(id)) {
            mSelectedSourceBtn.setSelected(false);
            mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
            mSoundBtn.setSelected(false);
            mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
            mSettingBtn.setSelected(false);
            mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
        } else if(SETTING.equals(id)) {
            mSelectedSourceBtn.setSelected(false);
            mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
            mSoundBtn.setSelected(false);
            mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
            mHDBtn.setSelected(false);
            mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
        } else {
            mSelectedSourceBtn.setSelected(false);
            mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
            mSoundBtn.setSelected(false);
            mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
            mHDBtn.setSelected(false);
            mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
            mSettingBtn.setSelected(false);
            mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
        }
    }

    /**
     * 设置播放控制按钮状态
     * @param id
     * @param isSelected
     */
    public void setPlayControlBtn(String id,boolean isSelected) {

        if(SELECTED_SOURCE.equals(id)) {
            if(isSelected) {
                mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_click);
            } else {
                if(mSelectedSourceBtn.isFocused()) {
                    mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_hover);
                } else {
                    mSelectedSourceBtn.setImageBg(R.drawable.play_icon_function_number_normal);
                }
            }
            mSelectedSourceBtn.setSelected(isSelected);
        } else if(SOUND.equals(id)) {
            if(isSelected) {
                mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_click);
            } else {
                if(mSoundBtn.isFocused()) {
                    mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_hover);
                } else {
                    mSoundBtn.setImageBg(R.drawable.play_icon_function_voice_normal);
                }
            }
            mSoundBtn.setSelected(isSelected);
        } else if(HD.equals(id)) {
            if(isSelected) {
                mHDBtn.setImageBg(R.drawable.play_icon_function_definition_click);
            } else {
                if(mHDBtn.isFocused()) {
                    mHDBtn.setImageBg(R.drawable.play_icon_function_definition_hover);
                } else {
                    mHDBtn.setImageBg(R.drawable.play_icon_function_definition_normal);
                }
            }
            mHDBtn.setSelected(isSelected);
        } else if(SETTING.equals(id)) {
            if(isSelected) {
                mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_click);
            } else {
                if(mSettingBtn.isFocused()) {
                    mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_hover);
                } else {
                    mSettingBtn.setImageBg(R.drawable.play_icon_function_setting_normal);
                }
            }
            mSettingBtn.setSelected(isSelected);
        }
    }

    /**
     * true 显示播放按钮，false 显示暂停按钮
     * @param playOrPause
     */
    public void setPlayOrPauseBtn(boolean playOrPause) {
        if(playOrPause) {
            if(mPlayOrPauseBtn.isFocused()) {
                mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_play_hover);
            } else {
                mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_play_normal);
            }
        } else {
            if(mPlayOrPauseBtn.isFocused()) {
                mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_pause_hover);
            } else {
                mPlayOrPauseBtn.setBackground(R.drawable.play_icon_function_pause_normal);
            }
        }
        playFlag = playOrPause;
    }

    private void createBottomView() {
        mBottomView = new GLLinearView(mContext);
        mBottomView.setOrientation(GLConstant.GLOrientation.HORIZONTAL);
        mBottomView.setAlign(GLConstant.GLAlign.CENTER_VERTICAL);
        mBottomView.setLayoutParams(mWidth-80f,60f);
        mBottomView.setMargin(40f,140f,40f,0f);

        mCurrentTime = new GLTextView(mContext);
        mCurrentTime.setLayoutParams(100f,40f);
        mCurrentTime.setTextSize(24);
        mCurrentTime.setTextColor(new GLColor(0x666666));
        mCurrentTime.setText("00:00:00");
//        mCurrentTime.setBackground(new GLColor(0xff0000));
        mCurrentTime.setMargin(0f,10f,0f,0f);
        mCurrentTime.setPadding(0f,3f,0f,0f);

        mName = new GLTextView(mContext);
        mName.setLayoutParams(920f-200f,60f);
        mName.setTextSize(28);
        mName.setTextColor(new GLColor(0x666666));
        mName.setText("");
        mName.setAlignment(GLTextView.ALIGN_CENTER);
//        mName.setBackground(new GLColor(0x00ff00));
        mName.setPadding(10f,10f,10f,0f);

        mTotalTime = new GLTextView(mContext);
        mTotalTime.setLayoutParams(100f,40f);
        mTotalTime.setTextSize(24);
        mTotalTime.setTextColor(new GLColor(0x666666));
        mTotalTime.setText("00:00:00");
        mTotalTime.setAlignment(GLTextView.ALIGN_RIGHT);
//        mTotalTime.setBackground(new GLColor(0x0000ff));
        mTotalTime.setMargin(0f,10f,0f,0f);
        mTotalTime.setPadding(0f,3f,0f,0f);

        mBottomView.addView(mCurrentTime);
        mBottomView.addView(mName);
        mBottomView.addView(mTotalTime);

        mRootView.addView(mBottomView);
    }

    private void createSeekBarView() {

        seekBarView = new GLSeekBarView(mContext);
        seekBarView.setMargin(40f,120f,40f,0);
        HeadControlUtil.bindView(seekBarView);

        mRootView.addView(seekBarView);
    }
    /**
     * 更新当前加载进度
     * @param process
     */
    public void setProcess(int process) {
        seekBarView.setProcess(process);
    }
    /**
     * 更新预加载进度
     * @param process
     */
    public void setPrestrainProcess(int process) {
//        seekBarView.setPrestrainProcess(process);
    }

    private IPlayerControlCallBack mCallBack;
    private IViewVisiableListener mVisiableCallBack;

    /**
     * 播放控制回调
     * @param callBack
     */
    public void setIPlayerControlCallBack(IPlayerControlCallBack callBack){
        mCallBack = callBack;
        if(seekBarView!=null) {
            seekBarView.setIPlayerControlCallBack(callBack);
        }
    }


    public void setOnViewVisiableListener(IViewVisiableListener listener){
        mVisiableCallBack = listener;
    }

    /**
     * 更新进度
     * @param current
     * @param duration
     */
    public void updateProgress(int current,int duration){
//        MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
//            @Override
//            public void run() {
                if(seekBarView!=null) {
                    final int progress = (int) ((float) current / duration * 100);
                    MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
                        @Override
                        public void run() {
                            seekBarView.setProcess(progress);
                        }
                    });

                }
                if(mCurrentTime!=null){
                    String cur = TimeFormat.format(current/1000);
                    mCurrentTime.setText(cur);
                }
                if(mTotalTime!=null) {
                    String dur = TimeFormat.format(duration / 1000);
                    mTotalTime.setText(dur);
                }
//            }
//        });
    }

    public void updateDisplayDuration(long duration){
        if(seekBarView!=null) {
            seekBarView.updateDisplayDuration(duration);
        }
    }

    /**
     * 最大显示24个中文字符
     * @param name
     */
    public void setName(String name){
        if(!TextUtils.isEmpty(name)) {
            int length = 24;
            if (name.length() > length) {
                name = name.substring(0, length) +"...";
            }
            mName.setText(name);
        } else {
            mName.setText("");
        }
    }

    /**
     * 重置所有点击按钮状态并隐藏
     */
    public void hideAllView() {
        //重置所有点击按钮
        resetOtherButton("");
        mRootView.setVisible(false);
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(false);
        }
    }

    public void showAllView() {
        mRootView.setVisible(true);
        if(mVisiableCallBack!=null){
            mVisiableCallBack.onVisibility(true);
        }
    }

    /**
     * true ：暂停   false：播放
     * @return
     */
    public boolean isPlayFlag(){
        return playFlag;
    }

    public void setType(int type) {
        mType = type;
        switch (mType) {
            case MOVIE:
                setMovie();
                break;
            case LOCAL_MOVIE:
                break;
            case PANO:
                setPano();
                break;
            case LOCAL_PANO:
                break;
        }
    }

    private void setMovie() {
        mSelectedSourceBtn.setVisible(true);
        mSoundBtn.setVisible(true);
        mHDBtn.setVisible(true);
        mSettingBtn.setVisible(true);
    }

    private void setPano() {
        mSelectedSourceBtn.setVisible(false);
        mSoundBtn.setVisible(true);
        mHDBtn.setVisible(true);
        mSettingBtn.setVisible(false);
    }

    public void setBtnShow(String id,boolean isShow) {
        if(SELECTED_SOURCE.equals(id)) {
            mSelectedSourceBtn.setVisible(isShow);
        } else if(SOUND.equals(id)) {
            mSoundBtn.setVisible(isShow);
        } else if(HD.equals(id)) {
            mHDBtn.setVisible(isShow);
        } else if(SETTING.equals(id)) {
            mSettingBtn.setVisible(isShow);
        }
    }
}
