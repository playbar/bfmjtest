package com.baofeng.mj.business.accountbusiness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.baofeng.mj.R;
import com.baofeng.mj.bean.Response;
import com.baofeng.mj.bean.UserInfo;
import com.baofeng.mj.business.permissionbusiness.PermissionUtil;
import com.baofeng.mj.business.publicbusiness.BaseApplication;
import com.baofeng.mj.business.spbusiness.UserSpBusiness;
import com.baofeng.mj.ui.activity.ChangePhoneActivity;
import com.baofeng.mj.ui.activity.ImageCropActivity;
import com.baofeng.mj.ui.activity.UserRenameActivity;
import com.baofeng.mj.ui.dialog.ChangeHeadPortraitDialog;
import com.baofeng.mj.util.fileutil.FileCommonUtil;
import com.baofeng.mj.util.fileutil.FileStorageUtil;
import com.baofeng.mj.util.netutil.ApiCallBack;
import com.baofeng.mj.util.netutil.UserInfoApi;
import com.baofeng.mj.util.publicutil.Common;
import com.baofeng.mj.util.publicutil.ImageUtil;
import com.baofeng.mj.util.publicutil.NetworkUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by zhaominglei on 2016/5/28.
 * 用户信息编辑
 */
public class UserInfoEditBusiness {

    public int REQUEST_CODE_SNAPSHOT = 100;

    public int REQUEST_CODE_ALBUM = 101;

    public int REQUEST_CROP_IMAGE = 102;

    public int HEAD_PORTRAIT_UPDATE_SUCCESS = 103;

    public int HEAD_PORTRAIT_UPDATE_FAIL = 104;

    public int NET_EXCEPTION = 105;

    public int NICK_NAME_NULL = 106;

    public int NICK_NAME_TOO_SHORT = 107;

    public int NICK_NAME_TOO_LONG = 108;

    public int NICK_NAME_VALID = 109;

    public int NICK_NAME_UPDATE_SUCCESS = 110;

    public int NICK_NAME_UPDATE_FAIL = 111;

    public int NICK_NAME_INVALID = 112;

    public String IMAGE_PATH = "imagePath";

    private int HEAD_PORTRAIT_SIZE = 200;

    private String HEAD_PORTRAIT_DEFAULT_NAME = "default";

    private IUserInfoEditCallback userInfoEditCallback;

    private static UserInfoEditBusiness instance;

    private String snapshotDir;

    private File snapshot;

    private UserInfoEditBusiness() {
    }

    public static UserInfoEditBusiness getInstance() {
        synchronized (UserInfoEditBusiness.class) {
            if (instance == null) {
                instance = new UserInfoEditBusiness();
            }
            return instance;
        }
    }

    public void setUserInfoEditCallback(IUserInfoEditCallback userInfoEditCallback) {
        this.userInfoEditCallback = userInfoEditCallback;
    }

    /***
     * 更换头像 弹框
     */

    public void changeHeadPortrait(Activity context) {
        ChangeHeadPortraitDialog dialog = new ChangeHeadPortraitDialog(context);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALBUM) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (!TextUtils.isEmpty(uri.getAuthority())) {
                    Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (cursor == null) {
                        Toast.makeText(context, "图片没找到", Toast.LENGTH_SHORT).show();
                    } else {
                        cursor.moveToFirst();
                        startImageCropActivity(context, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));//裁剪图片
                        cursor.close();
                    }
                } else {
                    startImageCropActivity(context, uri.getPath());//裁剪图片
                }
            }
        } else if (requestCode == REQUEST_CODE_SNAPSHOT) {
            if (snapshot != null && snapshot.exists()) {
                startImageCropActivity(context, snapshot.getAbsolutePath());//裁剪图片
            }
        } else if (requestCode == REQUEST_CROP_IMAGE) {
            //照片裁剪成功
        }
    }

    /***
     * 截取图片
     *
     * @param context
     * @param url
     */
    private void startImageCropActivity(Activity context, String url) {
        Intent intent = new Intent(context, ImageCropActivity.class);
        intent.putExtra(IMAGE_PATH, url);
        context.startActivityForResult(intent, REQUEST_CROP_IMAGE);
    }

    /***
     * 本地相册
     *
     * @param context
     */
    public void selectFromAlbum(Activity context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        context.startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /***
     * 拍照
     *
     * @param context
     */
    public void selectFromCarema(Activity context) {
        if (TextUtils.isEmpty(snapshotDir)) {
            snapshotDir = FileStorageUtil.getMojingDir() + "/head";
        }
        if (PermissionUtil.isOverMarshmallow() && !PermissionUtil.hasSelfPermissions(context, Manifest.permission.CAMERA)) {
            Toast.makeText(context, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        FileStorageUtil.mkdir(snapshotDir);
        snapshot = new File(snapshotDir, String.valueOf((new Date()).getTime()) + ".png");
        Uri u = Uri.fromFile(snapshot);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
        context.startActivityForResult(intent, REQUEST_CODE_SNAPSHOT);
    }

    public Bitmap createClipZoomImg(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 1024 * 1024);
        opts.inJustDecodeBounds = false;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
            int degree = ImageUtil.readPictureDegree(path);
            if (degree > 0) {
                bitmap = ImageUtil.rotaingImageView(degree, bitmap);
            }
            return bitmap;
        } catch (OutOfMemoryError err) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 更新用户头像
     *
     * @param bitmap
     */
    public void updateHeadPortrait(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        saveHeadPortrait(bitmap);
        final File headPortrait = new File(getHeadPortraitDir() + File.separator + headPortraitName());
        new UserInfoApi().uploadHeadPortrait(headPortrait, new ApiCallBack<Response<String>>() {
            @Override
            public void onSuccess(Response<String> result) {
                super.onSuccess(result);
                UserSpBusiness.getInstance().setLogoUrl(((Response<String>) result).data);
                onUserInfoEditCallback(HEAD_PORTRAIT_UPDATE_SUCCESS, null);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
                    onUserInfoEditCallback(HEAD_PORTRAIT_UPDATE_FAIL, null);
                } else {
                    onUserInfoEditCallback(NET_EXCEPTION, null);
                }
            }
        });
    }

    /***
     * 保存截取的头像
     *
     * @param bitmap
     */
    private void saveHeadPortrait(Bitmap bitmap) {
        try {
            //缩放到200*200
            bitmap = ImageUtil.zoomBitmap(bitmap, HEAD_PORTRAIT_SIZE, HEAD_PORTRAIT_SIZE);
            String path = getHeadPortraitDir() + File.separator + headPortraitName();
            FileCommonUtil.deleteFile(path);
            Common.saveFile(bitmap, getHeadPortraitDir(),
                    headPortraitName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getHeadPortraitDir() {
        if (TextUtils.isEmpty(snapshotDir)) {
            snapshotDir = FileStorageUtil.getMojingDir() + "/head";
        }
        FileStorageUtil.mkdir(snapshotDir);
        return snapshotDir;
    }

    private String headPortraitName() {
        UserInfo userInfo = UserSpBusiness.getInstance().getUserInfo();
        if (TextUtils.isEmpty(userInfo.getUid())) {
            return "head_" + HEAD_PORTRAIT_DEFAULT_NAME + ".png";
        } else {
            return "head_" + userInfo.getUid() + ".png";
        }
    }

    public int checkNickName(String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            return NICK_NAME_NULL;
        }
        int charCount = Common.getCharCount(nickName);
        if (charCount < 4) {//昵称长度不能少于4个字符
            return NICK_NAME_TOO_SHORT;
        } else if (charCount > 30) {
            return NICK_NAME_TOO_LONG;
        } else if (!Common.checkUserName(nickName)) {
            return NICK_NAME_INVALID;
        } else {
            return NICK_NAME_VALID;
        }
    }

    public void updateNickName(final String nickName) {
        int checkRet = checkNickName(nickName);
        if (checkRet == NICK_NAME_VALID) {
            new UserInfoApi().setUserNickName(nickName, new ApiCallBack<Response<String>>() {
                @Override
                public void onSuccess(Response<String> result) {
                    super.onSuccess(result);
                    if (result instanceof Response
                            && ((Response) result).status) {
                        onUserInfoEditCallback(NICK_NAME_UPDATE_SUCCESS, null);
                        UserSpBusiness.getInstance().setNickName(nickName);
                    } else {
                        onUserInfoEditCallback(NICK_NAME_UPDATE_FAIL, result instanceof Response ? ((Response) result).msg : null);
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    onUserInfoEditCallback(NICK_NAME_UPDATE_FAIL, null);
                    if (NetworkUtil.isNetworkConnected(BaseApplication.getInstance())) {
                        onUserInfoEditCallback(NICK_NAME_UPDATE_FAIL, null);
                    } else {
                        onUserInfoEditCallback(NET_EXCEPTION, null);
                    }
                }
            });
        } else {
            onUserInfoEditCallback(checkRet, null);
        }
    }

    private void onUserInfoEditCallback(int code, Object obj) {
        if (userInfoEditCallback != null) {
            userInfoEditCallback.onUserInfoEditCallback(code, obj);
        }
    }

    public void startUserRenameActivity(Context context) {
        Intent intent = new Intent(context, UserRenameActivity.class);
        context.startActivity(intent);
    }

    public void startChangePhoneActivity(Context context) {
        Intent intent = new Intent(context, ChangePhoneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public interface IUserInfoEditCallback {
        public void onUserInfoEditCallback(int code, Object obj);
    }
}
