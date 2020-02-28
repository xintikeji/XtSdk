package com.zt.httplibrary;


import com.zt.httplibrary.Response.OldResponseResult;
import com.zt.httplibrary.Response.ResponseResult;
import com.zt.httplibrary.Response.ResponseResultSkeleton;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface CommonQueueService
{
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("")
    Observable<OldResponseResult> postRxBody(@Url String interfaceName, @HeaderMap Map<String, String> headers, @Body Map<String, Object> reqParamMap);

    @GET
    Observable<String> postGetBody(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                   @QueryMap Map<String, Object> queryMap);

    //新式 restful api 接口调用
    @GET
    Observable<Response<ResponseResult>> postRestfulGetRxBody(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                                              @QueryMap Map<String, Object> queryMap);

    @Headers({"Content-Type: application/json"})
    @PUT
    Observable<Response<ResponseResult>> postRestfulPutRxBody(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                                              @Body Map<String, Object> fieldMap);

    @Headers({"Content-Type: application/json"})
    @PUT
    Observable<String> postRestfulPutRxBody2(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                             @Body Object fieldMap);

    @POST
    Observable<Response<ResponseResult>> postRestfulPostRxBody(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                                               @Body Map<String, Object> fieldMap);

//    @DELETE
    @HTTP(method = "DELETE", hasBody = true)
    Observable<Response<ResponseResult>> postRestfulDeleteRxBody(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                                                 @Body Map<String, Object> fieldMap);

    @POST
    Observable<Response<ResponseResult>> postRestfulPostRxBodyWithResponse(@Url String interfaceName, @HeaderMap Map<String, String> headers,
                                                                           @Body Map<String, Object> fieldMap);


    @GET
    Observable<String> postRestfulWPGetRxBody(@Url String interfaceName, @QueryMap Map<String, Object> queryMap);

    //提交一个POST表单
    @FormUrlEncoded
    @POST("queue")
    Observable<ResponseResultSkeleton> postRxString(@FieldMap Map<String, String> reqParamMap);

    //提交一个POST表单
    @FormUrlEncoded
    @POST("queue")
    Flowable<ResponseResultSkeleton> postRx2String(@FieldMap Map<String, String> reqParamMap);


    @Multipart
    @POST
    Observable<Response<ResponseResult>> uploadImg(@Url String interfaceName, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part params);

    @Multipart
    @POST
    Observable<Response<ResponseResult>> uploadImgs(@Url String interfaceName, @HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

}
