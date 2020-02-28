package com.xt.library.repositories

import com.google.gson.Gson
import com.xt.library.api.RequestHeader
import com.xt.library.beans.MyInfoBean
import com.zt.httplibrary.HttpMethods
import com.zt.httplibrary.Response.ResponseResult
import io.reactivex.Observer
import retrofit2.Response

/**
 * @author admin
 * @Date 2019/3/14
 * @Version 1.0
 */
class RequestRepository {


    fun tokenLogin(observer: Observer<Response<ResponseResult>>) {
        var reqMap = RequestHeader.getCommonPartOfParam()
        val headerMap = RequestHeader.getCommonHeaderOfParam()
        HttpMethods.getInstance().startRestfulPostRequestWithResponse(observer, RequestHeader.USER + RequestHeader.LOGIN, headerMap, reqMap, true)
    }

    fun getMyInfo(observer: Observer<MyInfoBean>) {
        val reqMap = RequestHeader.getCommonPartOfParam()
        val headerMap = RequestHeader.getCommonHeaderOfParam()
        HttpMethods.getInstance().startRestfulGetRequest(observer, { s ->
            Gson().fromJson(s, MyInfoBean::class.java)
        }, RequestHeader.MAINT_STAFF + "/my", headerMap, reqMap)
    }


    //获取电梯对应设备sip号和设备唯一识别码
    fun getSipAndCode(elevEquipmentCode: String, observer: Observer<String>) {
        val reqMap = RequestHeader.getCommonPartOfParam()
        val headerMap = RequestHeader.getCommonHeaderOfParam()
        HttpMethods.getInstance().startRestfulGetRequest(observer, RequestHeader.INFO  + "$elevEquipmentCode/${RequestHeader.BIND}", headerMap, reqMap, true)
    }

    //获取时间戳
    fun getTimestamp(sipId: String, observer: Observer<String>) {
        val reqMap = RequestHeader.getCommonPartOfParam()
        val headerMap = RequestHeader.getCommonHeaderOfParam()
        HttpMethods.getInstance().startRestfulGetRequest(observer, RequestHeader.TIMESTAMP + sipId, headerMap, reqMap, true)
    }
    //更新时间戳。
    fun uploadLookTimestamp(sipId: String, observer: Observer<String>) {
        val reqMap = RequestHeader.getCommonPartOfParam()
        val headerMap = RequestHeader.getCommonHeaderOfParam()
        HttpMethods.getInstance().startRestfulPutRequest(observer, RequestHeader.GET_TIMESTAMP + sipId, headerMap, reqMap, true)
    }
}
