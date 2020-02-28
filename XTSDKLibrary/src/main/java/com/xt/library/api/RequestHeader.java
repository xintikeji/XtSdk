package com.xt.library.api;


import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2018 科技发展有限公司
 * 完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2018/4/20 9:47
 * @description 发起HTTP请求需要的常量表
 */
public class RequestHeader
{
    public static final String clientType="weibao";

    public static final String AUTHORIZATION = "Authorization";
    public static final String CLIENT_TYPE = "Client-Type";
    public static final String BEARER = "Bearer";//token
    public static final String BASIC = "Basic";//用户名密码

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String SORT="sort";
    public static final String FIELDS = "fileds";


    public static String appToken = "";
    public static final String USER = "user/";
    public static final String LOGIN = "login";
    public static final String MAINT_STAFF = "maintstaff";
    public static final String INFO = "info/";
    public static final String BIND = "bind";
    public static final String TIMESTAMP = "timestamp/";
    public static final String GET_TIMESTAMP = "getTimeStamp/";

    public static final String CLIENT_DEVICE_ID = "clientDeviceId";

    public static Map<String, Object> getCommonPartOfParam()
    {
        Map<String, Object> reqParamMap = new HashMap<>();
//        reqParamMap.put(TOKEN, SharePreUtils.getToken());
//        reqParamMap.put(DEVICE_ID, CommonUtils.getDeviceId());
        return reqParamMap;
    }
    public static Map<String, Object> getCommonPartOfParam(int index, int pageNum, String fields)
    {
        Map<String, Object> reqParamMap = new HashMap<>();
        reqParamMap.put(RequestHeader.PAGE, index);
        reqParamMap.put(RequestHeader.LIMIT, pageNum);
        reqParamMap.put(RequestHeader.FIELDS, fields);
        return reqParamMap;
    }
    public static Map<String, Object> getCommonPartOfParam(int index, int pageNum)
    {
        Map<String, Object> reqParamMap = new HashMap<>();
        reqParamMap.put(RequestHeader.PAGE, index);
        reqParamMap.put(RequestHeader.LIMIT, pageNum);
        return reqParamMap;
    }
//
    public static Map<String, String> getCommonHeaderOfParam()
    {
        Map<String, String> reqHeaderMap = new HashMap<>();
        String token =appToken;
//        if (!TextUtils.isEmpty(token))
//            reqHeaderMap.put(RequestHeader.AUTHORIZATION, BEARER + " " + token);
            reqHeaderMap.put(RequestHeader.AUTHORIZATION, token);
        reqHeaderMap.put(CLIENT_TYPE, clientType);
//        String deviceId = CommonUtils.getDeviceId();
        String deviceId = "996352bece82c7cc";
        reqHeaderMap.put(CLIENT_DEVICE_ID, deviceId);
        return reqHeaderMap;
    }
    public static Map<String, String> getCommonSWHeaderOfParam()
    {
        Map<String, String> reqHeaderMap = new HashMap<>();
//        String token = SharePreUtils.getSWToken();
//        if (!TextUtils.isEmpty(token))
//            reqHeaderMap.put(AUTHORIZATION, BEARER + " " + token);
//
//        reqHeaderMap.put(X_SiteWhere_Tenant_Id, Api.SiteWhere_X_SiteWhere_Tenant_Id);
//        reqHeaderMap.put(X_SiteWhere_Tenant_Auth, Api.SiteWhere_X_SiteWhere_Tenant_Auth);
        return reqHeaderMap;
    }




}