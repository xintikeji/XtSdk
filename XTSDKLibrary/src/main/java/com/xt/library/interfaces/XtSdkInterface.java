package com.xt.library.interfaces;

import android.app.Activity;
import android.content.Context;

/**
 *
 */
public interface XtSdkInterface {


    void init(Context application, String token,XtApiInterface.OnInfoEvents onEvents);
    //    void getMyInfo(Context context,XtApiInterface.OnInfoEvents onEvents);
    void getVideoIntercom(Context context,String elevEquipmentCode, XtApiInterface.OnVideoIntercomEvents onVideoEvents);
    void getVideoMonitoring(String elevEquipmentCode,XtApiInterface.OnVideoMonitoringEvents onVideoEvents);
    void getElevatorStatus(String elevEquipmentCode,XtApiInterface.OnElevatorStatusEvents onVideoEvents);

    /**
     * 释放视频监控组件
     */
    void videoEnd();
}
