package com.xt.library.interfaces;

import android.content.Context;

import com.xt.library.beans.ElevatorStatusInfo;
import com.xt.library.beans.MqMessageBean;
import com.xt.library.interfaces.impl.XtSdkInterfaceImpl;
import com.zt.linphonelibrary.LinphoneUtils;



public class XtApiInterface {

    public final static XtSdkInterface app = new XtSdkInterfaceImpl();

    /**
     * 初始化接口
     *
     * @param application application
     * @param appToken    用户token
     */
    public static void init(Context application, String appToken, OnInfoEvents onEvents) {
        app.init(application, appToken, onEvents);
    }

    /**
     * 视频对讲
     *
     * @param elevEquipmentCode
     */
    public static void getVideoIntercom(Context context, String elevEquipmentCode, OnVideoIntercomEvents onVideoEvents) {
        app.getVideoIntercom(context, elevEquipmentCode, onVideoEvents);
    }
    /**
     * 视频对讲-设置扬声器
     * @param isSpeakerEnabled
     */
    public static void setSpeaker(boolean isSpeakerEnabled) {
        LinphoneUtils.toggleSpeaker(isSpeakerEnabled);
    }
    /**
     * 视频对讲-设置麦克风静音
     * @param isMicMuter
     */
    public static void setMicMuter(boolean isMicMuter) {
        LinphoneUtils.toggleMicro(isMicMuter);
    }
    /**
     * 视频对讲-挂断
     */
    public static void videoHangUp() {
        LinphoneUtils.hangUp();
    }
    /**
     * 视频监控
     *
     * @param elevEquipmentCode
     */
    public static void getVideoMonitoring(String elevEquipmentCode, OnVideoMonitoringEvents onVideoEvents) {
        app.getVideoMonitoring(elevEquipmentCode, onVideoEvents);
    }
    /**
     * 视频监控-结束释放资源
     */
    public static void videoEnd() {
        app.videoEnd();
    }

    /**
     * 电梯运行状态
     *
     * @param elevEquipmentCode
     */
    public static void getElevatorStatus(String elevEquipmentCode, OnElevatorStatusEvents onVideoEvents) {
        app.getElevatorStatus(elevEquipmentCode, onVideoEvents);
    }


    public interface OnInfoEvents {
        //        void myInfoResult(MyInfoBean myInfoBean);
        void requestResult(String msg);
    }

    public interface OnVideoIntercomEvents {
        void videoIntercomResult(String url,String msg);//视频对讲回调
    }

    public interface OnVideoMonitoringEvents {
        void videoMonitoringResult(String url,String msg);//视频监控回调
    }

    public interface OnElevatorStatusEvents {
        void elevatorStatusResult(ElevatorStatusInfo elevatorInfo);//电梯运行状态
    }


}
