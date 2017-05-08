package com.baofeng.mj.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.NewFile;
import com.baofeng.mj.ui.adapter.LocalFileBrowseAdapter;
import com.baofeng.mj.util.diskutil.DiskStatFs;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.VideoExtensionUtil;
import com.baofeng.mj.util.viewutil.StartActivityHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowseActivity extends BaseActivity {
    private LinearLayout ll_finish;
    private LinearLayout ll_back;
    private TextView tv_back;
    private ListView lv_root;
    private ListView lv_common;
    private LocalFileBrowseAdapter rootAdapter;
    private LocalFileBrowseAdapter commonAdapter;
    private List<File> rootFileList;
    private List<File> commonFileList;
    private List<Integer> positionList = new ArrayList<Integer>();//记录点击位置
    private boolean onItemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_local_file_browse);
        ll_finish = (LinearLayout) findViewById(R.id.ll_finish);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        lv_root = (ListView) findViewById(R.id.lv_root);
        lv_common = (ListView) findViewById(R.id.lv_common);
        setOnClick();//设置点击事件
        rootFileList = new ArrayList<File>();
        commonFileList = new ArrayList<File>();
        initRootFileList();//初始化根目录文件集合
        rootAdapter = new LocalFileBrowseAdapter(this, rootFileList, true);
        commonAdapter = new LocalFileBrowseAdapter(this, commonFileList, false);
        lv_root.setAdapter(rootAdapter);
        lv_common.setAdapter(commonAdapter);
    }

    private void setOnClick(){
        //进入下一级目录
        lv_root.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lv_root.setVisibility(View.GONE);
                lv_common.setVisibility(View.VISIBLE);
                ll_back.setVisibility(View.VISIBLE);
                boolean refreshSuccess = refreshCommonFileList(rootFileList.get(position));//刷新文件列表
                if (refreshSuccess) {
                    positionList.add(position);//记录当前点击位置
                }
            }
        });
        //进入下一级目录
        lv_common.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = commonFileList.get(position);
                if (file.isFile()) {//文件
                    if(VideoExtensionUtil.fileIsVideo(file)){//是视频文件
                        if(onItemClick){
                            return;//点击过，直接返回
                        }
                        onItemClick = true;//置为true，不让点击
                        String fileName = file.getName();
                        String filePath = file.getAbsolutePath();
                        showProgressDialog();//显示加载进度条
                        StartActivityHelper.playVideoWithLocal(FileBrowseActivity.this, fileName, filePath, new StartActivityHelper.GotoPlayCallback() {
                            @Override
                            public void callback() {//去播放的回调
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissProgressDialog();//隐藏加载进度条
                                        onItemClick = false;
                                    }
                                });
                            }
                        });
                    }
                } else {//文件夹
                    boolean refreshSuccess = refreshCommonFileList(file);//刷新文件列表
                    if (refreshSuccess) {
                        positionList.add(position);//记录当前点击位置
                    }
                }
            }
        });
        //返回上一级目录
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();//返回上一级目录
            }
        });
        //返回
        ll_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(View.VISIBLE == lv_root.getVisibility()){
                finish();
            }else{
                back();//返回上一级目录
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回上一级目录
     */
    private void back(){
        if (commonFileList != null && commonFileList.size() > 0) {
            File parentFile = commonFileList.get(0).getParentFile();
            if (parentFile != null) {
                if (isRootDir(parentFile.getAbsolutePath())) {//根目录
                    lv_root.setVisibility(View.VISIBLE);
                    lv_common.setVisibility(View.GONE);
                    ll_back.setVisibility(View.GONE);
                    if (positionList.size() > 0) {
                        lv_root.setSelection(positionList.get(positionList.size() - 1));
                        positionList.remove(positionList.size() - 1);
                    }
                } else {
                    refreshCommonFileList(parentFile.getParentFile());//刷新文件列表
                    if (positionList.size() > 0) {
                        lv_common.setSelection(positionList.get(positionList.size() - 1));
                        positionList.remove(positionList.size() - 1);
                    }
                }
            }
        }
    }

    /**
     * 初始化根目录文件集合
     */
    private void initRootFileList(){
        String externalStoragePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        String[] tempPathArr = FileStorageUtil.getAllStorageDir();
        final long MB = 1024 * 1024;
        int count = 1;
        for (int i = 0; i < tempPathArr.length; i++){
            String filePath = tempPathArr[i];
            long totalSize = DiskStatFs.getStatFsTotal(filePath);//总大小
            if(totalSize / MB > 500){//总的大小大于500MB
                NewFile file = new NewFile(filePath);
                if(!TextUtils.isEmpty(externalStoragePath) && externalStoragePath.equals(filePath)){
                    file.setNewName("手机存储");
                }else{
                    file.setNewName("外部存储" + count);
                    count++;
                }
                rootFileList.add(file);
            }
        }
    }

    /**
     * 刷新文件列表
     */
    private boolean refreshCommonFileList(File file){
        if(file != null && file.isDirectory()) {//文件夹
            File[] fileArr = file.listFiles();//文件夹下所有子文件
            if(fileArr != null && fileArr.length > 0){
                commonFileList.clear();
                for (File childFile : fileArr){
                    commonFileList.add(childFile);
                }
                commonAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    /**
     * 是不是根目录
     */
    private boolean isRootDir(String filePath){
        for (File file : rootFileList){
            if(filePath.equals(file.getAbsolutePath())){
                return true;//true是根目录
            }
        }
        return false;//不是根目录
    }
}
