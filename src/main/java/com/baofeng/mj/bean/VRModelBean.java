package com.baofeng.mj.bean;

import com.baofeng.mj.business.videoplayer.vrSurface.VrModel;

/**
 * Created by zhaominglei on 2016/6/22.
 */
public class VRModelBean {

    private int id;

    private VrModel.ScreenType screenType;

    private VrModel.ModelType modelType;

    private String name;

    private boolean isShowDivider;

    public VRModelBean() {

    }

    public VRModelBean(VrModel.ScreenType type, String name) {
        this.screenType = type;
        this.name = name;
    }

    public VRModelBean(VrModel.ScreenType type, VrModel.ModelType modelType, String name) {
        this.screenType = type;
        this.modelType = modelType;
        this.name = name;
    }

    public VRModelBean(VrModel.ScreenType type, VrModel.ModelType modelType, String name, boolean isShowDivider) {
        this.screenType = type;
        this.modelType = modelType;
        this.name = name;
        this.isShowDivider = isShowDivider;
    }

    public VRModelBean(int id, VrModel.ScreenType type, VrModel.ModelType modelType, String name, boolean isShowDivider) {
        this.id = id;
        this.screenType = type;
        this.modelType = modelType;
        this.name = name;
        this.isShowDivider = isShowDivider;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VrModel.ScreenType getScreenType() {
        return screenType;
    }

    public void setScreenType(VrModel.ScreenType type) {
        this.screenType = type;
    }

    public VrModel.ModelType getModelType() {
        return modelType;
    }

    public void setModelType(VrModel.ModelType modelType) {
        this.modelType = modelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowDivider() {
        return isShowDivider;
    }

    public void setShowDivider(boolean showDivider) {
        isShowDivider = showDivider;
    }
}
