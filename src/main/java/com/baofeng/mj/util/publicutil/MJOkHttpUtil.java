package com.baofeng.mj.util.publicutil;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by linzanxian on 2015/12/1.
 */
public class MJOkHttpUtil {
    private static MJOkHttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Gson mGson;
    private Handler mDelivery;
    private long t1;
    //Executor exec =  new ThreadPoolExecutor( 5 ,  10 ,  1 ,   TimeUnit.SECONDS,  new LinkedBlockingQueue<Runnable>());
    Executor exec = Executors.newCachedThreadPool();
    private OkHttpClient.Builder mBuild;

    private static final String TAG = "OkHttpUtil";

    private MJOkHttpUtil() {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        Cache cache = new Cache(new File(Common.getSavePath() +"cache"), cacheSize);

        mOkHttpClient = new OkHttpClient();
        mBuild = mOkHttpClient.newBuilder();
//        mBuild.cache(cache);
        mBuild.connectTimeout(30, TimeUnit.SECONDS);
        mBuild.readTimeout(30, TimeUnit.SECONDS);

        mGson = new Gson();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static MJOkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (MJOkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new MJOkHttpUtil();
                }
            }
        }

        return mInstance;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _getAsyn(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();

        return execute;
    }

    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final ResultCallback callback) {
        t1 = System.currentTimeMillis();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _postAsyn(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();

        return response;
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步基于post的文件上传
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }


    private void _getloadFile(String url,final String filename,final String path){
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String line = null;
                StringBuffer sb = new StringBuffer();
                InputStream inputStream = null;
                FileOutputStream outputStream  = null;
                try {
                    File dir = new File(path);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = new File(dir,filename);
                    if(file.exists()){
                        file.delete();
                    }
                    file.createNewFile();


                     outputStream = new FileOutputStream(file);
                      inputStream = response.body().byteStream();
                          byte[] buffer = new byte[4096];
                            int readLength = 0;
                              while ((readLength=inputStream.read(buffer)) > 0) {
                                     byte[] bytes = new byte[readLength];
                                       System.arraycopy(buffer, 0, bytes, 0, readLength);
                                      outputStream.write(bytes);
                                  }

                             outputStream.flush();

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(outputStream!=null){
                        outputStream.close();
                    }
                    if(inputStream!=null){
                        inputStream.close();
                    }
                }


            }
        });

    }


    //*************对外公布的方法************
    public static Response getAsyn(String url) throws IOException
    {
        //getInstance().checkCertificates(url);
        return getInstance()._getAsyn(url);
    }

    public static String getAsString(String url) throws IOException {
        //getInstance().checkCertificates(url);
        Response execute = getInstance()._getAsyn(url);

        return execute.body().string();
    }

    public static void getAsyn(String url, ResultCallback callback)
    {
        //getInstance().checkCertificates(url);
        getInstance()._getAsyn(url, callback);
    }

    public static Response postAsyn(String url, Param... params) throws IOException
    {
        //getInstance().checkCertificates(url);
        return getInstance()._postAsyn(url, params);
    }

    public static String postAsString(String url, Param... params) throws IOException
    {
//        getInstance().checkCertificates(url);
        Response execute = getInstance()._postAsyn(url, params);

        return execute.body().string();
    }

    public static void postAsyn(String url, final ResultCallback callback, Param... params)
    {
//        getInstance().checkCertificates(url);
        getInstance()._postAsyn(url, callback, params);
    }


    public static void postAsyn(String url, final ResultCallback callback, Map<String, String> params)
    {
//        getInstance().checkCertificates(url);
        getInstance()._postAsyn(url, callback, params);
    }


    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException
    {
//        getInstance().checkCertificates(url);
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response post(String url, File file, String fileKey) throws IOException
    {
//        getInstance().checkCertificates(url);
        return getInstance()._post(url, file, fileKey);
    }

    public static Response post(String url, File file, String fileKey, Param... params) throws IOException
    {
//        getInstance().checkCertificates(url);
        return getInstance()._post(url, file, fileKey, params);
    }

    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException
    {
//        getInstance().checkCertificates(url);
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }


    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException
    {
//        getInstance().checkCertificates(url);
        getInstance()._postAsyn(url, callback, file, fileKey);
    }


    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException
    {
//        getInstance().checkCertificates(url);
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    /**
     * get请求网络保存文件到本地
     * @param url
     * @param saveFileName  本地保存文件的名字
     * @param saveFileDir   保存路径
     */
    public static void getAsynloadFile(String url,String saveFileName,String saveFileDir){
        getInstance()._getloadFile(url,saveFileName,saveFileDir);
    }

    //****************************
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params)
    {
        params = validateParam(params);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (Param param : params)
        {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null)
        {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++)
            {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                                "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private Param[] validateParam(Param[] params)
    {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params)
    {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries)
        {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private static final String SESSION_KEY = "Set-Cookie";
    private static final String mSessionKey = "JSESSIONID";

    private Map<String, String> mSessions = new HashMap<String, String>();

    private void deliveryResult(final ResultCallback callback, Request request)
    {
        mOkHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        sendFailedStringCallback(call.request(), e, callback);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("test2", "url："+call.request().url().toString());
                ResponseAsyncTask responseAsyncTask = new ResponseAsyncTask(callback);
                responseAsyncTask.executeOnExecutor(exec, response);
            }
        });
    }

    /**
     * 异步处理数据
     */
    public class ResponseAsyncTask extends AsyncTask<Response, Integer, String> {
        private Response mResponse;
        private ResultCallback mCallback;
        private Exception mE;

        public ResponseAsyncTask(ResultCallback callback) {
            mCallback = callback;
        }

        @Override
        protected String doInBackground(Response... params) {
            mResponse = params[0];
            InputStream in = null;
            String string = "";
            try {
                in = mResponse.body().byteStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int i = -1;
                while ((i = in.read()) != -1) {
                    baos.write(i);
                }
                string = baos.toString();
            } catch (Exception e) {
                string = "";
                mE = e;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        string = "";
                        mE = e;
                    }
                }
            }

            return string;
        }

        @Override
        protected void onPostExecute(String string) {
            if ("".equals(string)) {
                sendFailedStringCallback(mResponse.request(), mE, mCallback);
            } else {
                try {
                    String url = mResponse.request().url().toString();
                    //Log.d("test2", "url："+ url +"---response.body.time:"+ (System.currentTimeMillis()-t1));
                    Type type = mCallback.mType;
                    if (type == String.class) {
                        sendSuccessResultCallback(string, mCallback, url);
                    } else if (type == JSONObject.class) {
                        JSONObject o = new JSONObject(string);
                        sendSuccessResultCallback(o, mCallback, url);
                    } else if (type == JSONArray.class) {
                        JSONArray o = new JSONArray(string);
                        sendSuccessResultCallback(o, mCallback, url);
                    } else {
                        Object o = mGson.fromJson(string, mCallback.mType);
                        sendSuccessResultCallback(o, mCallback, url);
                    }
                } catch (Exception e) { //Json解析的错误
                    sendFailedStringCallback(mResponse.request(), e, mCallback);
                }
            }

            mResponse = null;
            mCallback = null;
            mE = null;
        }
    };

    //失败处理
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback)
    {
        if (callback != null) {
            callback.onError(request, e);
        }
    }

    //成功处理
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback, final String url)
    {
        //Log.d("test2", "sendSuccessResultCallback" + (System.currentTimeMillis() - t1));
        if (callback != null) {
            callback.onResponse(object, url);
        }
    }

    private Request buildPostRequest(String url, Param[] params)
    {
        if (params == null)
        {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params)
        {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    public static abstract class ResultCallback<T>
    {
        Type mType;
        public ResultCallback()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response, String url);
    }

    public static class Param
    {
        public Param()
        {
        }

        public Param(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    /*private void checkCertificates(String url) {
        if(!TextUtils.isEmpty(url)){
            if (url.startsWith("https://")) {
                try {
                    if (url.contains("game.mojing.cn")) {
                        setCertificates(App.getInstance().getApplicationContext().getAssets().open("game.mojing.cn.crt"));
                    } else if(url.contains("static.mojing.cn")){
                        setCertificates(App.getInstance().getApplicationContext().getAssets().open("static.mojing.cn.crt"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    public  void setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init (
                    null,
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom()
            );
            //Log.d("test2", "setSslSocketFactory");
            mBuild.sslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            //Log.d("test2", "error");
            e.printStackTrace();
        }
    }
}
