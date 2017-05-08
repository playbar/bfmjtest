package com.baofeng.mj.util.publicutil;

import android.text.TextUtils;

import com.baofeng.mj.util.fileutil.FileCommonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by qiguolong on 2016/2/3.
 */
public class SubUtil {

    private static String EXTENSION_SRT = "srt";

    private static String EXTENSION_SSA = "ssa";

    private static String EXTENSION_ASS = "ass";

    public static void parseInnerSubLists(Object extra, ArrayList<String> subtilesList) {
        String jsonString = extra.toString();
        if (TextUtils.isEmpty(jsonString) || "NULL".equals(jsonString)) {
            return;
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            if (jsonObj.length() == 0) {
                return;
            }

            JSONArray jsonArray = jsonObj.getJSONArray("Subtitle");
            // 添加底层返回字幕选项
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
//                subtilesList.add("(内嵌)"+jObj.get("language").toString()
                subtilesList.add("(内嵌)"+jObj.get("language").toString()
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描字幕
     * @param videoPath
     * @param subTitleList 添加的字幕路径list
     */
    public  static void addSubtitlePlug(String videoPath, ArrayList<String> subTitleList) {
        File vfile = new File(videoPath);
        if (vfile.exists()){
            File[] files = vfile.getParentFile().listFiles();
            if (files==null)
                return;
            for (File file : files) {
                String listFileExtension = FileCommonUtil.getFileNameSuffixNoDot(file.getName());
                if ((listFileExtension.equals(EXTENSION_ASS) || listFileExtension.equals(EXTENSION_SRT) || listFileExtension
                        .equals(EXTENSION_SSA)) ) {
                    if (file != null) {
                        String abSolutePath = file.getAbsolutePath();
                        if (!subTitleList.contains(abSolutePath)) {
                            subTitleList.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}
