package com.bfmj.sdk.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baofeng.mojing.MojingSDK;
import com.bfmj.sdk.common.App;
import com.bfmj.sdk.entity.GlassesItemMode;
import com.bfmj.sdk.entity.GlassesTypeMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 眼镜-镜片 管理类
 *
 * @author wanghongfang
 * @date 2015-08-17 11:50
 */
public class GlassesManager {
    private static GlassesManager instance;
    public static List<GlassesItemMode> datas = null;
    public static String MJ2_KEY = "MJ2_KEY";     // "H32ZZN-93F5XV-ZKFLWG-SYXNCW-Y32TFL-4WFU9R";
    public static String MJ3_A_96_KEY = "MJ3_A_96_KEY";            //"FPQ8D2-2NHGWY-93S32F-DXD8YG-9QDCSG-444YZT";
    public static String MJ3_B_60_KEY = "MJ3_B_60_KEY";          //"XHEEQ3-9HQ38V-SY9NX3-QZ8R8G-S8H8ZL-F823FK";
    public static String MJ3_KEY = "MJ3_KEY";                //"WGQ8DQ-YLEB99-A444CH-4WSHCB-23QUE8-ZGFLED";
    public static String MJ4_KEY = "MJ4_KEY";                //"QQXTDW-87WT9F-WH2NA4-9WE9QF-FRYRZF-49W3FT";
    public static String MJ_MOVICE_KEY = "MJ_MOVICE_KEY";
    public static String MJ_VRBOX_KEY = "MJ_VRBOX_KEY";
    public static String MJ_MOKE_KEY = "MJ_MOKE_KEY";
    public static String MJ_D_KEY = "MJ_D_KEY";
    private static final String MJ2_NAME = "魔镜1、魔镜2、小魔镜";
    private static final String MJ3_NAME = "魔镜3 (FOV98°)";
    private static final String MJ3P_A_NAME = "魔镜3 Plus A (FOV96°)";
    private static final String MJ3P_B_NAME = "魔镜3 Plus B (FOV60°)";
    private static final String MJ4_NAME = "魔镜4 (FOV96°)";
    private static final String MJ_MOVIE_NAME = "观影镜";
    private static final String MJ_VRBOX_NAME = "魔镜VR box";
    private static final String MJ_MOKE_NAME = "魔镜Moke";
    private static final String MJ_D_NAME = "魔镜小D (FOV60°)";
    public Context context;

    private GlassesManager(Context context) {
        this.context = context;
        try {
            if (!MojingSDK.GetInitSDK()) {
                MojingSDK.Init(context.getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initDefaultKey();
    }

    public static GlassesManager getInstance(Context context) {
        if (instance == null) {
            instance = new GlassesManager(context);
        }
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(context);
        }
        return instance;
    }

    /**
     * 解析json数据
     *
     * @param json
     * @param typeArray 解析的数组类型
     * @return 列表
     */
    private List<GlassesTypeMode> parserJson(String json, String typeArray) {
        if (TextUtils.isEmpty(json))
            return null;
        ArrayList<GlassesTypeMode> lists = new ArrayList<GlassesTypeMode>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            String className = "";
            if (jsonObj.has("ClassName")) {
                className = jsonObj.getString("ClassName");
            }
            String releaseDate = "";
            if (jsonObj.has("ReleaseDate")) {
                releaseDate = jsonObj.getString("ReleaseDate");
            }
            JSONArray arry = jsonObj.getJSONArray(typeArray);
            if (arry != null && arry.length() > 0) {
                for (int i = 0; i < arry.length(); i++) {
                    JSONObject obj = arry.getJSONObject(i);
                    String display = "";
                    if (obj.has("Display")) {
                        display = obj.getString("Display");
                    }
                    String url = "";
                    if (obj.has("URL")) {
                        url = obj.getString("URL");
                    }
                    String key = obj.getString("KEY");
                    String id = "";
                    if (obj.has("ID")) {
                        id = obj.getString("ID");
                    }
                    GlassesTypeMode model = new GlassesTypeMode(className, releaseDate, display, url, key, id);
                    lists.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lists;
    }

    /**
     * 获取选择的眼镜列表
     *
     * @return 显示到页面的数据列表
     * @author wanghongfang
     * @date 2015-08-19 13:09
     */
    public List<GlassesItemMode> getItemList() {
//        Log.d("login","----getItemList datas = "+datas);
        if (datas != null && datas.size() > 0)
            return datas;
        String json = MojingSDK.GetManufacturerList("ZH");
//        Log.d("login","----getItemList json = "+json);
        if (!TextUtils.isEmpty(json) && json.contains("ERROR")) {
            return null;
        }
        datas = new ArrayList<GlassesItemMode>();
        List<GlassesTypeMode> manuList = parserJson(json, "ManufacturerList");
        if (manuList == null || manuList.size() <= 0)
            return datas;
        final String glassesType = Common.getChannelCode(context,"GLASSES_TYPE");
        for (GlassesTypeMode typemode : manuList) {
            if (typemode == null)
                continue;
            String producJson = MojingSDK.GetProductList(typemode.getKEY(), "ZH");
            if (!TextUtils.isEmpty(producJson) && producJson.contains("ERROR")) {
                continue;
            }
            List<GlassesTypeMode> productData = parserJson(producJson, "ProductList");

            if (productData == null || productData.size() <= 0)
                continue;
            for (GlassesTypeMode producmode : productData) {
                if (producmode == null)
                    continue;
                String glassJson = null;
                try {
                    glassJson = MojingSDK.GetGlassList(producmode.getKEY(), "ZH");
                }catch (Exception e){

                }
//                Log.d("login","---glass Json = "+glassJson+"---productID = "+producmode.getID()+" manfid= "+typemode.getID());
                if (!TextUtils.isEmpty(glassJson) && glassJson.contains("ERROR")) {
                    continue;
                }
                List<GlassesTypeMode> glassesListdata = parserJson(glassJson, "GlassList");
                if (glassesListdata == null || glassesListdata.size() <= 0)
                    continue;
                for (GlassesTypeMode glassesMode : glassesListdata) {
                    if (glassesMode == null)
                        continue;


                    GlassesItemMode itemMode = new GlassesItemMode();
                    itemMode.setManufacturerName(typemode.getDisplay());
                    itemMode.setProductName(producmode.getDisplay());
                    itemMode.setProductKey(producmode.getKEY());
                    itemMode.setGlassesName(glassesMode.getDisplay());
                    itemMode.setGlassesKey(glassesMode.getKEY());
                    itemMode.setGlassesID(glassesMode.getID());
                    itemMode.setProductID(producmode.getID());
                    itemMode.setManufactureID(typemode.getID());
                    String displayName = getGlassDisplayName(typemode.getID(), producmode.getID(), glassesMode.getID());
                    itemMode.setDisplayName(displayName);
                    if ("14".equals(glassesMode.getID()) && "6".equals(producmode.getID()) && "1".equals(typemode.getID())) {
                        continue;
                    }
                    if ("230".equals(glassesMode.getID()) && "230".equals(producmode.getID()) && "230".equals(typemode.getID())) {
                        continue;
                    }
                    if ((MJ_VRBOX_NAME.equals(displayName)) && !("4".equals(glassesType))) {
                        continue;
                    }
                    if (MJ_MOKE_NAME.equals(displayName) && !("5".equals(glassesType))) {
                        continue;
                    }

                    if (!(MJ_MOVIE_NAME.equals(displayName))) {
                        datas.add(itemMode);
                    }


                }
            }
        }
        Collections.sort(datas, new Comparator<GlassesItemMode>() {

            @Override
            public int compare(GlassesItemMode lhs, GlassesItemMode rhs) {
                String manuf_name = lhs.getManufacturerName();
                String produc_name = lhs.getProductName();
                String glass_name = lhs.getGlassesName();
                String manu_name2 = rhs.getManufacturerName();
                String produc_name2 = rhs.getProductName();
                String glass_name2 = rhs.getGlassesName();
                String displayName = lhs.getDisplayName();
                String displayName2 = rhs.getDisplayName();
                if ("4".equals(glassesType)) {
                    if (MJ_VRBOX_NAME.equals(displayName)) {
                        return -1;
                    } else if (MJ_VRBOX_NAME.equals(displayName2)) {
                        return 1;
                    }
                } else if ("5".equals(glassesType)) {
                    if (MJ_MOKE_NAME.equals(displayName)) {
                        return -1;
                    } else if (MJ_MOKE_NAME.equals(displayName2)) {
                        return 1;
                    }
                }
                if (manuf_name.compareTo(manu_name2) == 0) {
                    if (produc_name.compareTo(produc_name2) == 0) {
                        return glass_name.compareTo(glass_name2);
                    } else {
                        return produc_name.compareTo(produc_name2);
                    }
                } else {
                    return manuf_name.compareTo(manu_name2);
                }

            }
        });
        return datas;
    }

    /**
     * 根据glasskey获取眼镜和镜片的信息
     *
     * @param strGlassKey
     * @return 眼镜信息mode
     */
    public GlassesItemMode getGlassesInfo(String strGlassKey) {
        String str = MojingSDK.GetGlassInfo(strGlassKey, "ZH");
        if (TextUtils.isEmpty(str))
            return null;
        if (!TextUtils.isEmpty(str) && str.contains("ERROR")) {
            return null;
        }
        try {
            JSONObject jsonObj = new JSONObject(str);
            String className = "";
            if (jsonObj.has("ClassName")) {
                className = jsonObj.getString("ClassName");
            }
            String releaseDate = "";
            if (jsonObj.has("ReleaseDate")) {
                releaseDate = jsonObj.getString("ReleaseDate");
            }
            JSONObject manufObj = jsonObj.getJSONObject("Manufacturer");
            String manuf_id = manufObj.getString("ID");
            String manuf_display = "";
            if (manufObj.has("Display")) {
                manuf_display = manufObj.getString("Display");
            }
            String manuf_url = "";
            if (manufObj.has("URL")) {
                manuf_url = manufObj.getString("URL");
            }
            JSONObject productObj = jsonObj.getJSONObject("Product");
            String produ_id = productObj.getString("ID");
            String produ_display = "";
            if (productObj.has("Display")) {
                produ_display = productObj.getString("Display");
            }
            String produ_url = "";
            if (productObj.has("URL")) {
                produ_url = productObj.getString("URL");
            }
            JSONObject GlassObj = jsonObj.getJSONObject("Glass");
            String Glass_id = GlassObj.getString("ID");
            String Glass_display = "";
            if (GlassObj.has("Display")) {
                Glass_display = GlassObj.getString("Display");
            }
            String Glass_url = "";
            if (GlassObj.has("URL")) {
                Glass_url = GlassObj.getString("URL");
            }
            GlassesItemMode itemMode = new GlassesItemMode();
            itemMode.setManufacturerName(manuf_display);
            itemMode.setProductName(produ_display);
            itemMode.setGlassesName(Glass_display);
            itemMode.setManufactureID(manuf_id);
            itemMode.setProductID(produ_id);
            itemMode.setGlassesID(Glass_id);
            itemMode.setDisplayName(getGlassDisplayName(manuf_id, produ_id, Glass_id));
            return itemMode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getGlassDisplayName(String manuf_id, String produ_id, String glass_id) {
        String displayName = MJ2_NAME;
        if ("1".equals(manuf_id) && "1".equals(produ_id) && "1".equals(glass_id)) {
            displayName = MJ2_NAME;
        } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "3".equals(glass_id)) {
            displayName = MJ3_NAME;
        } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "4".equals(glass_id)) {
            displayName = MJ3P_B_NAME;
        } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "11".equals(glass_id)) {
            displayName = MJ3P_A_NAME;
        } else if ("1".equals(manuf_id) && "3".equals(produ_id) && "12".equals(glass_id)) {
            displayName = MJ4_NAME;
        } else if ("1".equals(manuf_id) && "5".equals(produ_id) && "13".equals(glass_id)) {
            displayName = MJ_MOVIE_NAME;
        } else if ("1".equals(manuf_id) && "7".equals(produ_id) && "15".equals(glass_id)) {
            displayName = MJ_D_NAME;
        } else if ("200".equals(manuf_id) && "200".equals(produ_id) && "200".equals(glass_id)) {
            displayName = MJ_VRBOX_NAME;
        } else if ("260".equals(manuf_id) && "260".equals(produ_id) && "260".equals(glass_id)) {
            displayName = MJ_MOKE_NAME;
        }

        return displayName;
    }

    /**
     * 根据扫描获取的二维码得到enterMojing时需要的key
     *
     * @param productcode 眼镜二维码 可以为空
     * @param glassCode   镜片二维码
     * @return key
     */
    public String getGenerationGlassKey(String productcode, String glassCode) {
        String str = MojingSDK.GenerationGlassKey("", glassCode);
        if (!TextUtils.isEmpty(str) && str.contains("ERROR")) {
            return "";
        }
        GlassesItemMode info = getGlassesInfo(str);
        if (info == null || MJ_MOVIE_NAME.equals(info.getDisplayName())) {
            return "";
        }

        return str;
    }

    public void initDefaultKey() {
        try {
            if (!MojingSDK.GetInitSDK()) {
                MojingSDK.Init(context.getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("login","----initDefaultKey ----");
        List<GlassesItemMode> lists = getItemList();
        if (lists == null || lists.size() <= 0) {
            return;
        }
        for (GlassesItemMode mode : lists) {
            if (mode == null)
                continue;
            String manuf_id = mode.getManufactureID();
            String produ_id = mode.getProductID();
            String glass_id = mode.getGlassesID();
            if ("1".equals(manuf_id) && "1".equals(produ_id) && "1".equals(glass_id)) {
//                MJ2_KEY = mode.getGlassesKey();
//                Log.d("login","----initDefaultKey MJ2_key = "+mode.getGlassesKey());
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ2_KEY, mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "3".equals(glass_id)) {
//                MJ3_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ3_KEY,mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "4".equals(glass_id)) {
//                MJ3_B_60_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ3_B_60_KEY,mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "2".equals(produ_id) && "11".equals(glass_id)) {
//                MJ3_A_96_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ3_A_96_KEY,mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "3".equals(produ_id) && "12".equals(glass_id)) {
//                MJ4_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ4_KEY,mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "5".equals(produ_id) && "13".equals(glass_id)) {
//                MJ_MOVICE_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ_MOVICE_KEY,mode.getGlassesKey());
            } else if ("1".equals(manuf_id) && "7".equals(produ_id) && "15".equals(glass_id)) {
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ_D_KEY,mode.getGlassesKey());
            } else if ("200".equals(manuf_id) && "200".equals(produ_id) && "200".equals(glass_id)) {
//                MJ_VRBOX_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ_VRBOX_KEY,mode.getGlassesKey());
            } else if ("260".equals(manuf_id) && "260".equals(produ_id) && "260".equals(glass_id)) {
//                MJ_MOKE_KEY = mode.getGlassesKey();
                DefaultSharedPreferenceManager.getInstance(context).setString(MJ_MOKE_KEY,mode.getGlassesKey());
            }
        }
    }

    public String getDefaultGlassesKey() {
        String glassesType = Common.getChannelCode(context,"GLASSES_TYPE");
        Log.w("px","glassesType  "+glassesType);
        if ("0".equals(glassesType)) {

        } else if ("1".equals(glassesType)) {
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ3_A_96_KEY);
//            return GlassesManager.MJ3_A_96_KEY;
        } else if ("2".equals(glassesType)) {
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ3_B_60_KEY);
//            return GlassesManager.MJ3_B_60_KEY;
        } else if ("3".equals(glassesType)) {
//            return GlassesManager.MJ4_KEY;
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ4_KEY);

        } else if ("4".equals(glassesType)) {
//            return GlassesManager.MJ_VRBOX_KEY;
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ_VRBOX_KEY);
        } else if ("5".equals(glassesType)) {
//            return GlassesManager.MJ_MOKE_KEY;
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ_MOKE_KEY);
        } else if("6".equals(glassesType)){
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ_D_KEY);
        }else if("7".equals(glassesType)){
            return DefaultSharedPreferenceManager.getInstance(context).getString(MJ3_KEY);
        }
        return DefaultSharedPreferenceManager.getInstance(context).getString(MJ2_KEY);
//        return GlassesManager.MJ2_KEY;
    }

    /**
     * 获取启动游戏时要传递的镜片参数（manufactureid + productid+glassesid）
     *
     * @return json 格式的数据
     * @author wanghongfang
     * @date 20151210
     */
    public String getGameGlassesParams(String glasskey) {
        if (TextUtils.isEmpty(glasskey))
            return "";
        GlassesItemMode mode = getGlassesInfo(glasskey);
        if (mode == null)
            return "";
        try {
            JSONObject json = new JSONObject();
            json.put("manufactureid", mode.getManufactureID());
            json.put("productid", mode.getProductID());
            json.put("glassesid", mode.getGlassesID());
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public String getGlassesKeyByIds(String ids) {
        if (ids == null || "".equals(ids))
            return getDefaultGlassesKey();
        if (datas == null) {
//            datas = getItemList();
            initDefaultKey();
        }
        try {
            JSONObject obj = new JSONObject(ids);
            String manufId = obj.getString("manufactureid");
            String productid = obj.getString("productid");
            String glassesid = obj.getString("glassesid");
            for (GlassesItemMode mode : datas) {
                if (mode.getManufactureID().equals(manufId) && mode.getProductID().equals(productid) && mode.getGlassesID().equals(glassesid)) {
                    return mode.getGlassesKey();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getDefaultGlassesKey();
    }

    public interface DataCallBack {
        void onResult(ArrayList<GlassesTypeMode> datas);
    }

}
