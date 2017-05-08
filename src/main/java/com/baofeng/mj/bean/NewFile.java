package com.baofeng.mj.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by liuchuanchi on 2016/5/10.
 * 新的文件实体类
 */
public class NewFile extends File implements Serializable{
    private static final long serialVersionUID = -2906663245222306316L;
    private String newName;//新的文件名

    public NewFile(String path){
        super(path);
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
