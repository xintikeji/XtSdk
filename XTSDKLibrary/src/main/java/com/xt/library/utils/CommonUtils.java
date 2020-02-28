package com.xt.library.utils;


import android.content.Context;
import android.media.AudioManager;

/**
 * @author ZT
 * @Date 2018/6/30
 * @Version 1.0
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();
    public static String url = "http://xinti.dtznjg.com/";
    public static String mqttServer = "iot.dtznjg.com";
    public static String mqttServerPort = "2883";
    public static String sipServer = "121.204.184.32";
    public static String sipServerPort = "15060";

    /**
     * 获取系统媒体音量
     *
     * @param context
     * @return
     */
    public static int getSystemVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamVolume(3);// 3代表music
        return currentVolume;
    }

    /**
     * 设置系统媒体声音
     *
     * @param context
     * @param volume
     * @return
     */
    public static boolean setSystemVolume(Context context, int volume) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (volume >= 0 && volume < 16) {
            mAudioManager.setStreamVolume(3, volume, 0);
            return true;
        } else {
            return false;
        }
    }

}
