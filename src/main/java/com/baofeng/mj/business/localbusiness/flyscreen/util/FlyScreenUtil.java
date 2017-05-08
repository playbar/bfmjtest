package com.baofeng.mj.business.localbusiness.flyscreen.util;

import android.os.DropBoxManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mj.bean.BaseMessage;
import com.baofeng.mj.bean.BaseMessage.MessageType;
import com.baofeng.mj.bean.DeviceInfo;
import com.baofeng.mj.bean.Resource;
import com.baofeng.mj.bean.Resource.BasicResourceMessage;
import com.baofeng.mj.bean.Resource.RequestDirCount;
import com.baofeng.mj.bean.Resource.RequestPageData;
import com.baofeng.mj.bean.Resource.RequestServerPort;
import com.baofeng.mj.bean.Resource.ResourceMessageType;
import com.baofeng.mj.business.localbusiness.flyscreen.FlyScreenBusiness;
import com.baofeng.mj.business.localbusiness.flyscreen.logic.FlyScreenBaseModel;
import com.baofeng.mj.business.localbusiness.flyscreen.logic.FlyScreenLoginModel;
import com.baofeng.mj.business.localbusiness.flyscreen.logic.FlyScreenTcpSocket;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.publicutil.MJOkHttpUtil;
import com.baofeng.mojing.sdk.login.utils.OkHttpUtil;
import com.bfmj.sdk.util.StringUtil;
import com.bfmj.sdk.util.StringUtils;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: FlyScreenUtil <br/>
 *
 * @author qiguolong
 * @date: 2015-9-1 下午3:40:00 <br/>
 * @description:
 */
public class FlyScreenUtil {
    private static final int rowNumPrePage = 1000;

    public static enum FlyResType {
        video, music, game, picture
    };

    public static void sendMes(FlyScreenTcpSocket tcp, byte[] msg) {
        tcp.send(msg);
    }

    public static void sendVideoDataFromDir(FlyScreenTcpSocket tcp, String dir) {
        sendMes(tcp, createtDirPageRequest(FlyResType.video, dir, 0));
    }

    public static void sendVideoDataCount(FlyScreenTcpSocket tcp, String dir) {
        sendMes(tcp, createtDirCountRequest(FlyResType.video, dir));
    }

    /**
     * 创建获取资源服务器端口请求数据包
     *
     * @return
     */
    public byte[] createServerPortRequest() {
        return RequestModel.createtServerPortRequestDataPacket();
    }

    /**
     * 创建特定目录分页请求数据
     *
     * @param resType 资源类型
     * @param dirPath 目录路径
     * @param start   起始位置
     * @return
     */
    public static byte[] createtDirPageRequest(FlyResType resType,
                                               String dirPath, int start) {
        return RequestModel.createtDirPageRequestDataPacket(resType, dirPath,
                start);
    }

    /**
     * 创建目录下节点统计的请求
     *
     * @param resType
     * @param dirPath
     * @return
     */
    public static byte[] createtDirCountRequest(FlyResType resType,
                                                String dirPath) {
        return RequestModel.createtDirCountRequestDataPacket(resType, dirPath);
    }

    /**
     * 处理请求数据模型
     *
     * @author yanzw
     * @date 2014-7-4 下午5:55:34
     */
    private static class RequestModel {

        /**
         * 创建资源服务器地址请求数据包
         */
        public static byte[] createtServerPortRequestDataPacket() {
            // 创建业务数据包
            byte[] busMsg = createtServerPortRequestBusinessDataPacket();
            byte[] basicMsg = createtBasicResourceMessage(
                    BaseMessage.MessageType.MessageType_Video, busMsg);

            // tag包
            byte[] tag = getItagAppToPc_Message();
            // 发包总包
            byte[] totalMsg = new byte[8 + basicMsg.length];
            // 消息实体总长
            byte[] msgLen = intToByteArray(basicMsg.length);

            System.arraycopy(tag, 0, totalMsg, 0, 4);
            System.arraycopy(msgLen, 0, totalMsg, 4, 4);
            System.arraycopy(basicMsg, 0, totalMsg, 8, basicMsg.length);

            return totalMsg;
        }

        /**
         * 创建请求资源服务器端口的数据包
         *
         * @return
         */
        private static byte[] createtServerPortRequestBusinessDataPacket() {
            BasicResourceMessage.Builder baseBuilder = Resource.BasicResourceMessage
                    .newBuilder();
            baseBuilder
                    .setMt(ResourceMessageType.ResourceMessageType_RequestHttpServertPort);

            Resource.RequestServerPort.Builder detailBuilder = RequestServerPort
                    .newBuilder();
            detailBuilder.setSessionID(FlyScreenLoginModel.getSessionId());

            RequestServerPort detailData = detailBuilder.build();

            byte[] detailMsgByteAry = detailData.toByteArray();
            if (detailMsgByteAry != null) {
                String detailMsgStr = "";
                try {
                    detailMsgStr = new String(detailMsgByteAry, "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                baseBuilder.setDetailMsg(detailMsgStr);
            }

            BasicResourceMessage questMsg = baseBuilder.build();
            byte[] byteMsg = questMsg.toByteArray();
            return byteMsg;
        }

        /**
         * 创建目录分页数据请求数据包
         *
         * @param resType
         * @param dirPath
         * @param start
         * @return
         */
        public static byte[] createtDirPageRequestDataPacket(
                FlyResType resType, String dirPath, int start) {
            // 创建业务数据包
            ByteString busMsg = createtDirPageRequestBusinessDataPacket(
                    resType, dirPath, start);
            byte[] basicMsg = createtBasicResourceMessage2(busMsg);
            // tag包
            byte[] tag = getItagAppToPc_Message();
            // 发包总包
            byte[] totalMsg = new byte[8 + basicMsg.length];
            // 消息实体总长
            byte[] msgLen = intToByteArray(basicMsg.length);

            System.arraycopy(tag, 0, totalMsg, 0, 4);
            System.arraycopy(msgLen, 0, totalMsg, 4, 4);
            System.arraycopy(basicMsg, 0, totalMsg, 8, basicMsg.length);

            return totalMsg;
        }

        /**
         * 创建请求资源统计的数据包
         *
         * @return
         */
        private static ByteString createtDirPageRequestBusinessDataPacket(
                FlyResType resType, String dirPath, int start) {
            BasicResourceMessage.Builder baseBuilder = BasicResourceMessage
                    .newBuilder();
            baseBuilder
                    .setMt(ResourceMessageType.ResourceMessageType_RequestDirPageData);

            RequestPageData.Builder detailBuilder = RequestPageData
                    .newBuilder();
            detailBuilder.setSessionID(FlyScreenLoginModel.getSessionId());
            detailBuilder.setStartIndex(start);
            detailBuilder.setEndIndex(start + rowNumPrePage);
            detailBuilder.setUri(dirPath);
            RequestPageData pageData = detailBuilder.build();
            baseBuilder.setDetailMsgBytes(pageData.toByteString());
            BasicResourceMessage questMsg = baseBuilder.build();
            return questMsg.toByteString();
        }

        /**
         * 创建目录分页数据请求数据包
         *
         * @param resType
         * @param dirPath
         * @return
         */
        public static byte[] createtDirCountRequestDataPacket(
                FlyResType resType, String dirPath) {
            // 创建业务数据包
            ByteString busMsg = createtDirCountRequestBusinessDataPacket(
                    resType, dirPath);
            byte[] basicMsg = createtBasicResourceMessage2(busMsg);
            // tag包
            byte[] tag = getItagAppToPc_Message();
            // 发包总包
            byte[] totalMsg = new byte[8 + basicMsg.length];
            // 消息实体总长
            byte[] msgLen = intToByteArray(basicMsg.length);

            System.arraycopy(tag, 0, totalMsg, 0, 4);
            System.arraycopy(msgLen, 0, totalMsg, 4, 4);
            System.arraycopy(basicMsg, 0, totalMsg, 8, basicMsg.length);

            return totalMsg;
        }

        /**
         * 创建请求资源统计的数据包
         *
         * @return
         */
        private static ByteString createtDirCountRequestBusinessDataPacket(
                FlyResType resType, String dirPath) {
            BasicResourceMessage.Builder baseBuilder = BasicResourceMessage
                    .newBuilder();
            baseBuilder
                    .setMt(ResourceMessageType.ResourceMessageType_RequestDirCount);

            RequestDirCount.Builder detailBuilder = RequestDirCount
                    .newBuilder();
            detailBuilder.setSessionID(FlyScreenLoginModel.getSessionId());
            detailBuilder.setDirectory(dirPath);
            RequestDirCount dirCount = detailBuilder.build();
            baseBuilder.setDetailMsgBytes(dirCount.toByteString());

            BasicResourceMessage questMsg = baseBuilder.build();

            return questMsg.toByteString();
        }

        /**
         * 创建资源请求
         *
         * @return
         */
        private static byte[] createtBasicResourceMessage2(ByteString busMsg) {
            ByteString basicMsg = null;
            basicMsg = FlyScreenBaseModel.createBasicMassage(
                    MessageType.MessageType_Video, busMsg);

            return basicMsg.toByteArray();
        }

        /**
         * 创建资源请求的包包
         *
         * @param resType
         * @return
         */
        private static byte[] createtBasicResourceMessage(MessageType resType,
                                                          byte[] busMsg) {
            byte[] basicMsg = null;
            FlyScreenBaseModel.createBasicMassage(
                    // BaseMessage.MessageType.MessageType_Video
                    resType, busMsg);
            return basicMsg;
        }
    }

    public static String getResUriSmall(String uri, DeviceInfo deviceInfo) {
        try {
            return "http://" + deviceInfo.getIp() + ":"
                    + FlyScreenLoginModel.getServerPort() + uri
                    + "?snap={\"mode\":\"0\"}";
        }
        catch (Exception e) {
            return "";
        }


    }

    /**
     * url编码
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8")
                    .replace("%2F", "/").replace("+", "%20")
                    .replace("%28", "(").replace("%29", ")");
        }
        catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * 获取资源uri
     *
     * @param uri
     * @return
     */
    public static String getResUri(String uri, DeviceInfo deviceInfo) {
        try {
            return "http://" + deviceInfo.getIp() + ":"
                    + FlyScreenLoginModel.getServerPort() + uri;
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取飞屏播放的字幕文件路径
     * @param item
     * @param deviceInfo
     * @return
     */
    public static void getSutitleUri(Resource.PageItem item,  DeviceInfo deviceInfo){
        HashMap<String,String> subtitleList = new HashMap<>();
        try {
            if(item==null)
                return;
            if(item.getSubtitleType()==0)
                return ;
            String uri = FlyScreenUtil.urlEncode(item.getUri());
            String filename = StringUtils.getFileNameNoEx(item.getName());
            if(item.getSubtitleType()==0x1){//srt
                String str1 =  ("subtitle={\"mode\":\"1\"}");
                String srt =  "http://" + deviceInfo.getIp() + ":"
                        + FlyScreenLoginModel.getServerPort() + uri+"?"+str1;

                String subTitleName = filename+".srt";
                subtitleList.put(subTitleName,srt);


                saveSubtitleFileToLocal(subtitleList);

            }else if(item.getSubtitleType()==0x2) {//ass
                String ass =  "http://" + deviceInfo.getIp() + ":"
                        + FlyScreenLoginModel.getServerPort() + uri+"?subtitle={\"mode\":\"2\"}";

                String subTitleName = filename+".ass";
                subtitleList.put(subTitleName,ass);

                saveSubtitleFileToLocal(subtitleList);
            }else if(item.getSubtitleType()==(0x3)){//包含两种字幕
                String str =  "http://" + deviceInfo.getIp() + ":"
                        + FlyScreenLoginModel.getServerPort()+uri;
                String sub_srt = str+"?subtitle={\"mode\":\"1\"}";
                String sub_ass = str+"?subtitle={\"mode\":\"2\"}";


                String subTitleName_srt = filename+".srt";
                String subTitleName_ass = filename+".ass";
                subtitleList.put(subTitleName_srt,sub_srt);
                subtitleList.put(subTitleName_ass,sub_ass);

                saveSubtitleFileToLocal(subtitleList);
            }
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }

    /***
     * 获取字幕路径 并缓存到本地
     * @param pageItems
     */
    public static void saveSubtitleFile( List<Resource.PageItem> pageItems){
        if(pageItems==null||pageItems.size()<=0)
            return;

        for(Resource.PageItem pageItem:pageItems) {
            if(pageItem==null)
                continue;
            if(pageItem.getBDir())
                continue;

//            //获取字幕路径 并缓存到本地
            getSutitleUri(pageItem, FlyScreenBusiness.getInstance().getCurrentDevice());
        }

    }

    /**
     * 缓存飞屏字幕文件到本地
     * @param subtitleList
     */
    public static void saveSubtitleFileToLocal(HashMap<String,String> subtitleList){
        if(subtitleList==null||subtitleList.size()<=0)
            return;
        String path = FileStorageUtil.getMJFlyScreenSubFile();
        for (Map.Entry<String, String> item : subtitleList.entrySet()){
            String name = item.getKey(); //name
            String url = item.getValue(); //uri
            MJOkHttpUtil.getAsynloadFile(url,name,path);
        }


    }

    /**
     * 获取缓存好的飞屏字幕文件路径，播放时加载字幕使用
     * @param path 视频路径
     * @return
     */
    public static List<String> getSubtitleList(String path) {
        if(TextUtils.isEmpty(path))
            return null;
        String fileName = StringUtils.getFileName(path);  //包含扩展名的fileName
        String fileNoEx = StringUtils.getFileNameNoEx(fileName); //无扩展名的filename
        File root = new File(FileStorageUtil.getMJFlyScreenSubFile());
        if (!root.exists()) {
            return null;
        }
        List<String> sublist = new ArrayList<>();
        File[] files = root.listFiles();
        for (File file : files) {
            if(file==null||!file.exists()){
                continue;
            }
            String listFileExtension = StringUtils.getFileNameNoEx(file.getName());
            if (fileNoEx.equals(listFileExtension)) {

                    String abSolutePath = file.getAbsolutePath();
                    if (!sublist.contains(abSolutePath)) {
                        sublist.add(file.getAbsolutePath());
                    }

            }
        }

        return sublist;


    }


    public static int getPcToAppItagInt_Inner() {
        int tag = 4660; // 0x1234
        return tag;
    }

    public static byte[] getItagAppToPc_Inner() {
        int tag = 9029; // 0x2345

        byte[] itag = new byte[4];
        itag = intToByteArray(tag);
        return itag;
    }

    public static byte[] getItagAppToPc_Message() {
        int tag = 22136; // 0x5678

        byte[] itag = new byte[4];
        itag = intToByteArray(tag);
        return itag;
    }

    public static int getItagPcToAppInt_Message() {
        int tag = 26505; // 0x6789
        return tag;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
                | (b[0] & 0xFF) << 24;
    }
}
