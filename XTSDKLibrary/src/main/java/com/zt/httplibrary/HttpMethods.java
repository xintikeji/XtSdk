package com.zt.httplibrary;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zt.httplibrary.Response.CustomBean;
import com.zt.httplibrary.Response.OldResponseResult;
import com.zt.httplibrary.Response.ResponseResult;
import com.zt.httplibrary.converter.FastJsonConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Refactor by ben on 2017/3/30
 */
public class HttpMethods
{
    private static final String TAG = HttpMethods.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 20;

    private Retrofit retrofit;
    private Retrofit wpRestfulRetrofit;//wordpress
    private Retrofit swRestfulRetrofit;//sitewhere
    private Retrofit restfulRetrofit;
    private OkHttpClient okHttpClient;
    private final String clientType = "weibao";

    //private Gson g = new Gson();

    //构造方法私有
    private HttpMethods()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(logging);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);//设置可访问的网站
        okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))  //gson 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                .baseUrl(HttpUtils.Companion.getInstance().getBaseUrl())
                .build();
        restfulRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())  //gson 转换器
//                .addConverterFactory(GsonConverterFactory.create())  //gson 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                .baseUrl(HttpUtils.Companion.getInstance().getRestfulBaseUrl())
                .build();
        wpRestfulRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())  //gson 转换器
//                .addConverterFactory(GsonConverterFactory.create())  //gson 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                .baseUrl(HttpUtils.Companion.getInstance().getWpRestfulBaseUrl())
                .build();
        swRestfulRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())  //gson 转换器
//                .addConverterFactory(GsonConverterFactory.create())  //gson 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx 工厂。
                .baseUrl(HttpUtils.Companion.getInstance().getSwRestfulBaseUrl())
                .build();
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder
    {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    public OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

    /**
     * 处理网络请求结果，返回的是后台接口的body里面的字符串
     *
     * @param subscriber
     * @param interfaceName
     * @param reqParamMap
     * @param isObserveMainThread
     */
    @Deprecated
    public void startServerRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> reqParamMap, boolean isObserveMainThread)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(reqParamMap)));
        Observable<String> observable = service.postRxBody(interfaceName, headerMap, reqParamMap)
                .map(new OldZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }


    /**
     * 处理网络请求结果，将结果转换成的类型交给使用者处理
     * 此方法的优秀之处在于将数据处理完全放在了工作线程，转换成用户的目标类型后才切换到UI线程
     *
     * @param subscriber
     * @param mapper
     * @param interfaceName
     * @param reqParamMap
     * @param <T>
     */
    @Deprecated
    public <T> void startServerRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> reqParamMap)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(reqParamMap)));
        Observable<T> observable = service.postRxBody(interfaceName, headerMap, reqParamMap)
                .map(new OldZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public void startServerGetRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> reqParamMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(reqParamMap)));
        Observable<String> observable = service.postGetBody(interfaceName, headerMap, reqParamMap);
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startServerGetRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> reqParamMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(reqParamMap)));
        Observable<T> observable = service.postGetBody(interfaceName, headerMap, reqParamMap).map(mapper);
        toSubscribe(observable, subscriber, true);
    }


    public void startRestfulGetRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
//        Map<String, String> headers = new ArrayMap<>();
//        String token = SharePreUtils.getToken();
//        if (!TextUtils.isEmpty(token))
//            headers.put("Authorization", token);
//        headers.put("Client-Type", clientType);
//        String deviceId = CommonUtils.getDeviceId();
//        headers.put("clientDeviceId", deviceId);
        Observable<String> observable = service.postRestfulGetRxBody(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public void testStartRestfulGetRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
        Observable<String> observable = service.postRestfulPostRxBodyWithResponse(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }


    public <T> void startRestfulGetRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));

//        Map<String, String> headers = new ArrayMap<>();
//        String token = SharePreUtils.getToken();
//        if (!TextUtils.isEmpty(token))
//            headers.put("Authorization", token);
//        headers.put("Client-Type", clientType);
//        String deviceId = CommonUtils.getDeviceId();
//        headers.put("clientDeviceId", deviceId);
        Observable<T> observable = service.postRestfulGetRxBody(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public <T> void startRestfulGetRequestByLinpeng(Observer<T> subscriber, Function<CustomBean, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
        Observable<T> observable = service.postRestfulGetRxBody(interfaceName, headerMap, queryMap)
                .map(new LinpengZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    /**
     * restful api PUT 调用
     * 处理网络请求结果，返回的是后台接口的body里面的字符串
     *
     * @param subscriber
     * @param interfaceName
     * @param fieldMap
     * @param isObserveMainThread
     */
    public void startRestfulPutRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));
//        Map<String, String> headers = new ArrayMap<>();
//        String token = SharePreUtils.getToken();
//        if (!TextUtils.isEmpty(token))
//            headers.put("Authorization", token);
//        headers.put("Client-Type", clientType);
//        String deviceId = CommonUtils.getDeviceId();
//        headers.put("clientDeviceId", deviceId);
        Observable<String> observable = service.postRestfulPutRxBody(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }


    /**
     * restful api PUT 调用
     * 处理网络请求结果，将结果转换成的类型交给使用者处理
     * 此方法的优秀之处在于将数据处理完全放在了工作线程，转换成用户的目标类型后才切换到UI线程
     *
     * @param subscriber
     * @param mapper
     * @param interfaceName
     * @param queryMap
     * @param <T>
     */
    public <T> void startRestfulPutRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));

//        Map<String, String> headers = new ArrayMap<>();
//        String token = SharePreUtils.getToken();
//        if (!TextUtils.isEmpty(token))
//            headers.put("Authorization", token);
//        headers.put("Client-Type", clientType);
//        String deviceId = CommonUtils.getDeviceId();
//        headers.put("clientDeviceId", deviceId);
        Observable<T> observable = service.postRestfulPutRxBody(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public void startRestfulPostRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap,
                                        Map<String, Object> fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));

        Observable<String> observable = service.postRestfulPostRxBody(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startRestfulPostRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName,
                                            Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));


        Observable<T> observable = service.postRestfulPostRxBody(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public void startRestfulDeleteRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap,
                                          Map<String, Object> fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));

        Observable<String> observable = service.postRestfulDeleteRxBody(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startRestfulDeleteRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName,
                                              Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));

        Observable<T> observable = service.postRestfulDeleteRxBody(interfaceName, headerMap, queryMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }


    public void startRestfulUploadPic(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap,
                                      MultipartBody.Part fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));

        Observable<String> observable = service.uploadImg(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startRestfulUploadPic(Observer<T> subscriber, Function<String, T> mapper, String interfaceName,
                                          Map<String, String> headerMap, MultipartBody.Part fieldMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));


        Observable<T> observable = service.uploadImg(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public <T> void startRestfulPostRequestWithResponse(Observer<T> subscriber, String interfaceName,
                                                        Map<String, String> headerMap, Map<String, Object> queryMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));

        Observable<T> observable = (Observable<T>) service.postRestfulPostRxBodyWithResponse(interfaceName, headerMap, queryMap)
                .map(new ZTHttpResultFuncWithResponse());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public void startRestfulWPGetRequest(Observer<String> subscriber, String interfaceName, Map<String, Object> queryMap, boolean isObserveMainThread)
    {
        CommonQueueService service = wpRestfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
        Observable<String> observable = service.postRestfulWPGetRxBody(interfaceName, queryMap);
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public void startRestfulUploadPics(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, RequestBody> fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Observable<String> observable = service.uploadImgs(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc());
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startRestfulUploadPics(Observer<T> subscriber, Function<String, T> mapper, String interfaceName,
                                           Map<String, String> headerMap, Map<String, RequestBody> fieldMap)
    {
        CommonQueueService service = restfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(fieldMap)));


        Observable<T> observable = service.uploadImgs(interfaceName, headerMap, fieldMap)
                .map(new NewZTHttpResultFunc()).map(mapper);
        toSubscribe(observable, subscriber, true);
    }


    public void startRestfulSWGetRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap, boolean isObserveMainThread)
    {
        CommonQueueService service = swRestfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
        Observable<String> observable = service.postGetBody(interfaceName, headerMap, queryMap);
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

    public <T> void startRestfulSWGetRequest(Observer<T> subscriber, Function<String, T> mapper, String interfaceName, Map<String, String> headerMap, Map<String, Object> queryMap)
    {
        CommonQueueService service = swRestfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, new Gson().toJson(queryMap)));
        Observable<T> observable = service.postGetBody(interfaceName, headerMap, queryMap).map(mapper);
        toSubscribe(observable, subscriber, true);
    }

    public void startRestfulSWPutRequest(Observer<String> subscriber, String interfaceName, Map<String, String> headerMap, Object fieldMap, boolean isObserveMainThread)
    {
        CommonQueueService service = swRestfulRetrofit.create(CommonQueueService.class);
        Logger.t(TAG).d(String.format("接口请求数据：%s  %s ", interfaceName, fieldMap));
        Observable<String> observable = service.postRestfulPutRxBody2(interfaceName, headerMap, fieldMap);
        toSubscribe(observable, subscriber, isObserveMainThread);
    }

/*    public Flowable<ResponseResult> getApiService(String interfaceName, String isSync, Map<String, String> reqParamMap)
    {
        CommonQueueService service = retrofit.create(CommonQueueService.class);
        return service.postRx2String(createReqForm(interfaceName, isSync, reqParamMap))
                .map(new ResponseResultMapper());
    }*/

    //观察者启动器
    private <T> void toSubscribe(Observable<T> o, Observer<T> s, boolean isMainThread)
    {
        Scheduler observeScheduler = Schedulers.io();
        if (isMainThread)
            observeScheduler = AndroidSchedulers.mainThread();
        o.subscribeOn(Schedulers.io()) //绑定在io
                .observeOn(observeScheduler) //返回 内容 在Android 主线程
                .subscribe(s);  //放入观察者
    }

    /**
     * 组装消息体
     */
    private Map<String, Object> createReqBody(String interfaceName, Map<String, String> params)
    {
        Map<String, Object> m = new HashMap<>();
        Map<String, Object> clsm = new HashMap<>();
        clsm.put("reqName", interfaceName);
        m.put("head", clsm);
        m.put("body", params);
        Logger.t(TAG).d("接口请求数据：" + new Gson().toJson(m));
        return m;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class ZTHttpResultFunc implements Function<ResponseResult, String>
    {
        @Override
        public String apply(@NonNull ResponseResult httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
//            Logger.t(TAG).d("服务器返回结果" + MyApplication.getInstance().getGsonInstance().toJson(httpResult, ResponseResult.class));
            if (!"200".equals(httpResult.getCode()))
            {
                String bodyStr = httpResult.getMsg();
                String codeStr = httpResult.getCode();
//                if ("用户未登录".equals(bodyStr))
//                {
//                    Intent intent = new Intent(CommonConstant.ACTION_LOGIN_OUT_TIP);
//                    MyApplication.getInstance().sendBroadcast(intent);
//                }
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            return TextUtils.isEmpty(httpResult.getData()) ? "{}" : httpResult.getData();
        }
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class ZTHttpResultFuncWithResponse implements Function<Response<ResponseResult>, Response<ResponseResult>>
    {
        @Override
        public Response<ResponseResult> apply(@NonNull Response<ResponseResult> httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
            Logger.t(TAG).d("httpResponse.code:" + httpResult.code());
            Logger.t(TAG).d("httpResponse.body:" + httpResult.body());
            Logger.t(TAG).d("httpResponse.errBody:" + httpResult.errorBody());
            if (httpResult.code() != 200)
            {
                ResponseBody errBody = httpResult.errorBody();
                ResponseResult responseResult = new Gson().fromJson(errBody.string(), ResponseResult.class);
                String bodyStr = responseResult.getMsg();
                String codeStr = responseResult.getCode();
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            else
            {
                ResponseResult responseResult = httpResult.body();
//                Logger.t(TAG).d("responseResult:" + responseResult);
                if (!"200".equals(responseResult.getCode()))
                {
                    String bodyStr = responseResult.getMsg();
                    String codeStr = responseResult.getCode();
                    throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
                }
            }

            return httpResult;
        }
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class OldZTHttpResultFunc implements Function<OldResponseResult, String>
    {
        @Override
        public String apply(@NonNull OldResponseResult httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
//            Logger.t(TAG).d("服务器返回结果" + MyApplication.getInstance().getGsonInstance().toJson(httpResult, OldResponseResult.class));
            if ("false".equals(httpResult.getSuccess()))
            {
                String bodyStr = httpResult.getMsg();
                String codeStr = httpResult.getSuccess();
//                if ("用户未登录".equals(bodyStr))
//                {
//                    Intent intent = new Intent(CommonConstant.ACTION_LOGIN_OUT_TIP);
//                    MyApplication.getInstance().sendBroadcast(intent);
//                }
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            return TextUtils.isEmpty(httpResult.getResult()) ? "{}" : httpResult.getResult();
        }
    }

    /**
     * restful api调用处理  可处理http返回码 进行错误提示
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class NewZTHttpResultFunc implements Function<Response<ResponseResult>, String>
    {
        @Override
        public String apply(@NonNull Response<ResponseResult> httpResult) throws Exception
        {
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
            ResponseResult responseResult;
            if (httpResult.code() != 200)
            {
                ResponseBody errBody = httpResult.errorBody();
                responseResult = new Gson().fromJson(errBody.string(), ResponseResult.class);
                String bodyStr = responseResult.getMsg();
                String codeStr = responseResult.getCode();
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            else
            {
                responseResult = httpResult.body();
                if (!"200".equals(responseResult.getCode()) || TextUtils.isEmpty(responseResult.getCode()))
                {
                    String bodyStr = responseResult.getMsg();
                    String codeStr = responseResult.getCode();
                    throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
                }
            }
//            if(TextUtils.isEmpty(responseResult.getData())){
//                if(responseResult.getData()==null){
//                    responseResult.setData("");
//                }
//            }

            Logger.t(TAG).d("http请求结果：" + httpResult.code() + " 服务器返回结果" + new Gson().toJson(responseResult, ResponseResult.class));
            return TextUtils.isEmpty(responseResult.getData()) ? "{}" : responseResult.getData();
        }
    }


    /**
     * restful api调用处理  可处理http返回码 进行错误提示
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class LinpengZTHttpResultFunc implements Function<Response<ResponseResult>, CustomBean>
    {
        @Override
        public CustomBean apply(@NonNull Response<ResponseResult> httpResult) throws Exception
        {
            CustomBean bean = new CustomBean();
            if (httpResult == null)
            {
                throw new NullPointerException("|返回结果为null|");
            }
            ResponseResult responseResult;
            if (httpResult.code() != 200)
            {
                ResponseBody errBody = httpResult.errorBody();
                responseResult = new Gson().fromJson(errBody.string(), ResponseResult.class);
                String bodyStr = responseResult.getMsg();
                String codeStr = responseResult.getCode();
                throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
            }
            else
            {
                responseResult = httpResult.body();
                if (!"200".equals(responseResult.getCode()) || TextUtils.isEmpty(responseResult.getCode()))
                {
                    String bodyStr = responseResult.getMsg();
                    String codeStr = responseResult.getCode();
                    throw new ApiException(codeStr == null ? "" : codeStr, bodyStr == null ? "" : bodyStr);
                }
            }
            Logger.t(TAG).d("http请求结果：" + httpResult.code() + " 服务器返回结果" + new Gson().toJson(responseResult, ResponseResult.class));
//            return TextUtils.isEmpty(responseResult.getData()) ? "{}" : responseResult.getData();
            bean.setData(TextUtils.isEmpty(responseResult.getData()) ? "{}" : responseResult.getData());
            bean.setInterfaceName(httpResult.raw().request().url().toString());
            return bean;
        }
    }


    public void setBaseUser(String url) {
        retrofit = retrofit.newBuilder().baseUrl(url).build();
        swRestfulRetrofit = swRestfulRetrofit.newBuilder().baseUrl(url).build();
        restfulRetrofit = restfulRetrofit.newBuilder().baseUrl(url).build();
    }
}
