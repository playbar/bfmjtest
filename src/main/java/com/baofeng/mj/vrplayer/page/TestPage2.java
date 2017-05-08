package com.baofeng.mj.vrplayer.page;

import android.content.Context;

import com.baofeng.mj.R;
import com.baofeng.mj.vrplayer.adapter.EpisodeAdapter;
import com.baofeng.mj.vrplayer.utils.HeadControlUtil;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLAdapterView;
import com.bfmj.viewcore.view.GLGridView;
import com.bfmj.viewcore.view.GLGridViewScroll;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.ArrayList;

/**
 * Created by kekeyu on 2017/4/15.
 */

public class TestPage2 extends GLViewPage {

    private Context mContext;
    private GLRelativeView indexRootView;

    private GLGridViewScroll gridView;
    private EpisodeAdapter episodeAdapter;

    private ArrayList<Integer> list = new ArrayList<>();
    
    public TestPage2(Context context) {
        super(context);
        mContext = context;

        for(int x = 0; x < 100; x++) {
            list.add(x);
        }
    }

    @Override
    protected GLRectView createView(GLExtraData data) {
        indexRootView = new GLRelativeView(mContext);
        indexRootView.setLayoutParams(GLRectView.MATCH_PARENT,
                GLRectView.MATCH_PARENT);
        indexRootView.setBackground(new GLColor(0x414141));
        
        createGridView();
        
        return indexRootView;
    }

    private void createGridView() {
        gridView = new GLGridViewScroll( getContext(), 3, 2);
        gridView.setOrientation(GLConstant.GLOrientation.HORIZONTAL );
        gridView.setScrollDirection(GLGridView.ScrollDirection.UP_DOWN);
//        gridView.setX(400);
//        gridView.setY(800);
        gridView.setLayoutParams(315, 220);
        gridView.setBackground( new GLColor(1.0f, 0.50f, 0.50f ));
        gridView.setHorizontalSpacing( 20.0f);
        gridView.setVerticalSpacing( 20.0f);
        gridView.setBtnHorSpace( 0);
        gridView.setBottomSpaceing( 0);
//		gridView.setPadding( 10, 10, 10, 10);
		gridView.setMargin( 400, 800, 10, 10 );
//		gridView.setNumDefaultColor( new GLColor(1.0f, 0.0f, 1.0f ));
        gridView.setNumOnFouseColor( new GLColor(1.0f, 0.0f, 1.0f ));
        gridView.setFlipLeftIcon(R.drawable.flip_leftarrow);
        gridView.setFlipRightIcon( R.drawable.flip_rightarrow );
        gridView.setProcessBackground(R.drawable.play_slider_bg);
        gridView.setBarImage(R.drawable.play_slider_progress);
//        gridView.setOffsetY( 100);
        gridView.setBtnImageWidth(60);
        gridView.setBtnImageHeight(60);
        gridView.setProcessViewWidth(40);
        gridView.setProcessViewHeight(400);

        episodeAdapter = new EpisodeAdapter(mContext);

        gridView.setAdapter(episodeAdapter);

        setOnPageListener();

        indexRootView.addView(gridView);

        episodeAdapter.setData(list);

        gridView.setOnItemClickListener(new GLAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(GLAdapterView<?> glparent, GLView glview, int position, long id) {
//                System.out.println("!!!!!!!!!!!!!!!!!!---------------onItemClick");
            }
        });

        gridView.setOnItemSelectedListener(new GLAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(GLAdapterView<?> glparent, GLView glview, int position, long id) {
//                System.out.println("!!!!!!!!!!!!!!!!!!---------------onItemSelected");
            }

            @Override
            public void onNothingSelected(GLAdapterView<?> glparent, GLView glview, int position, long id) {
//                System.out.println("!!!!!!!!!!!!!!!!!!---------------onNothingSelected");
            }

            @Override
            public void onNoItemData() {

            }
        });
    }

    GLImageView prvBtnImgView;
    private void setOnPageListener() {
        prvBtnImgView = gridView.getPrvBtnImgView();
        prvBtnImgView.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(!gridView.isFirstPage()) {
                    if(focused) {
                        prvBtnImgView.setBackground(R.drawable.play_button_up_hover);
//                        gridView.setFlipLeftIcon(R.drawable.play_button_up_hover);
                    } else {
                        prvBtnImgView.setBackground(R.drawable.play_button_up_normal);
//                        gridView.setFlipLeftIcon(R.drawable.play_button_up_normal);
                    }
                }
            }
        });
        prvBtnImgView.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(gridView.isFirstPage()) {
                    gridView.setFlipLeftIcon(R.drawable.play_button_up_disable);
                } else {
                    gridView.previousPage();
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
        HeadControlUtil.bindView(prvBtnImgView);

        GLImageView nextBtnImgView = gridView.getNextBtnImgView();
        nextBtnImgView.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(!gridView.isLastPage()) {
                    if(focused) {
                        gridView.setFlipRightIcon( R.drawable.play_button_down_hover );
                    } else {
                        gridView.setFlipRightIcon( R.drawable.play_button_down_normal );
                    }
                }
            }
        });
        nextBtnImgView.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(gridView.isLastPage()) {
                    gridView.setFlipRightIcon(R.drawable.play_button_down_disable);
                } else {
                    gridView.nextPage();
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
        HeadControlUtil.bindView(nextBtnImgView);
    }
}
