package com.zt.linphonelibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.orhanobut.logger.Logger;
import com.xt.library.R;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneFriendList;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PayloadType;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;
import org.linphone.core.ToneID;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

/**
 * @author yqh
 * @Date 2018/12/10
 * @Version 1.0
 */
public class LinphoneService extends Service implements LinphoneCoreListener
{
    private static final String TAG = LinphoneService.class.getSimpleName();
    private static LinphoneService mInstance;
    private Context mContext;
    private LinphoneCore mLinphoneCore;
    private LinphoneCoreFactory lcFactory;
    private Timer mTimer;

    private String[] codecs = new String[]{"OPUS", "G722", "PCMU", "PCMA", "VP8", "H263", "H264"};

    public LinphoneService()
    {
    }

    public static boolean isReady()
    {
        return mInstance != null;
    }

    /**
     * @throws RuntimeException service not instantiated
     */
    public static LinphoneService instance()
    {
        if (isReady()) {
            return mInstance;
        }else {
//            throw new RuntimeException("LinphoneService not instantiated yet");
            return null;
        }
    }

    public static synchronized final LinphoneCore getLc()
    {
//        instance().mLinphoneCore.enableEchoCancellation(true);
//        instance().mLinphoneCore.enableEchoLimiter(true);
        return instance().mLinphoneCore;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
        mInstance = this;

        lcFactory = LinphoneCoreFactory.instance();
        lcFactory.setDebugMode(true, "ZTMaintenance");

        try
        {
            String basePath = mContext.getFilesDir().getAbsolutePath();
            copyAssetsFromPackage(basePath);
            mLinphoneCore = lcFactory.createLinphoneCore(this, basePath + "/.linphonerc", basePath + "/linphonerc", null, mContext);
            initLinphoneCoreValues(basePath);

            mLinphoneCore.addListener(this);

            mLinphoneCore.setNetworkReachable(true); // Let's assume it's true
            mLinphoneCore.enableEchoCancellation(true); //回声消除
            mLinphoneCore.enableAdaptiveRateControl(true);

            setUserAgent();
            setFrontCamAsDefault();
            startIterate();

//            LinphoneNatPolicy nat = LinphoneUtils.getOrCreateNatPolicy();
//            nat.enableIce(false);
//            mLinphoneCore.setNatPolicy(nat);
//
//            LinphoneProxyConfig config = LinphoneUtils.getOrCreateProxyConfig();
//            config.enableAvpf(false);
//            mLinphoneCore.setDefaultProxyConfig(config);

//

            for (PayloadType audioCodec : mLinphoneCore.getAudioCodecs())
            {
//                for (String codec : codecs)
//                {
//                    if (codec.equalsIgnoreCase(audioCodec.getMime()))
//                    {
//
//                        if ("PCMU".equals(audioCodec.getMime()))
//                        {
//                            LinphoneService.getLc().enablePayloadType(audioCodec, true);
//                            Logger.t(TAG).d("编码：" + audioCodec.getMime() + "，开启");
//                        }else{
//                            LinphoneService.getLc().enablePayloadType(audioCodec, false);
//                            Logger.t(TAG).d("编码：" + audioCodec.getMime() + "，关闭");
//                        }
//                        break;
//                    }
//                }
                if (audioCodec.getMime().equals("PCMU")) {
                    try {
                        android.util.Log.e(TAG, "setCodecMime: " + audioCodec.getMime() + " " + audioCodec.getRate());
                        LinphoneService.getLc().enablePayloadType(audioCodec, true);
                    } catch (LinphoneCoreException e) {
                        android.util.Log.e(TAG, "setCodecMime: " + e);
                    }
                } else {
                    try {
                        LinphoneService.getLc().enablePayloadType(audioCodec, false);
                    } catch (LinphoneCoreException e) {
                        e.printStackTrace();
                    }
                }
            }
//            for (PayloadType audioCodec : mLinphoneCore.getAudioCodecs())
//            {
//                Logger.t(TAG).d("audioCodec:" + audioCodec.getMime() + " | " + audioCodec.getRate() + " | " + audioCodec.getRecvFmtp() + " | " + audioCodec.getSendFmtp());
//                LinphoneService.getLc().enablePayloadType(audioCodec, getLc().isPayloadTypeEnabled(audioCodec));
//                for (String codec : codecs)
//                {
//                    if (codec.equalsIgnoreCase(audioCodec.getMime()))
//                    {
//                        LinphoneService.getLc().enablePayloadType(audioCodec, true);
//                        Logger.t(TAG).d("编码：" + audioCodec.getMime() + "，开启");
//                        break;
//                    }
//                    else
//                    {
//                        LinphoneService.getLc().enablePayloadType(audioCodec, false);
//                        Logger.t(TAG).d("编码：" + audioCodec.getMime() + "，关闭");
//
//                        if ("PCMU".equals(audioCodec.getMime()))
//                        {
//                            LinphoneService.getLc().enablePayloadType(audioCodec, true);
//                        }
//                        break;
//                    }
//                }
//            }

            Logger.t("LinphoneService").d("mLinphoneCore.getAudioCodecs():" + Arrays.toString(mLinphoneCore.getAudioCodecs()));

        } catch (LinphoneCoreException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.t(TAG).d("onDestroy：");
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if(mLinphoneCore!=null) {
            mLinphoneCore.removeListener(this);
            mLinphoneCore=null;
        }
        if(lTask!=null){
            lTask.cancel();
            lTask=null;
        }
        mInstance=null;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void registrationState(LinphoneCore linphoneCore, LinphoneProxyConfig linphoneProxyConfig, LinphoneCore.RegistrationState registrationState, String s)
    {

    }


    @Override
    public void globalState(LinphoneCore linphoneCore, LinphoneCore.GlobalState globalState, String s)
    {

    }

    @Override
    public void callState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s)
    {

    }

    /**
     * 从包内资源文件复制到手机SD卡
     *
     * @param basePath
     * @throws IOException
     */
    private void copyAssetsFromPackage(String basePath) throws IOException
    {
        LinphoneUtils.copyIfNotExist(mContext, R.raw.oldphone_mono, basePath + "/oldphone_mono.wav");
        LinphoneUtils.copyIfNotExist(mContext, R.raw.ringback, basePath + "/ringback.wav");
        LinphoneUtils.copyIfNotExist(mContext, R.raw.toy_mono, basePath + "/toy_mono.wav");
        LinphoneUtils.copyIfNotExist(mContext, R.raw.linphonerc_default, basePath + "/.linphonerc");
        LinphoneUtils.copyFromPackage(mContext, R.raw.linphonerc_factory, new File(basePath + "/linphonerc").getName());
        LinphoneUtils.copyIfNotExist(mContext, R.raw.lpconfig, basePath + "/lpconfig.xsd");
        LinphoneUtils.copyIfNotExist(mContext, R.raw.rootca, basePath + "/rootca.pem");
    }

    /**
     * 在linphoneCore中设置铃声等内容
     *
     * @param basePath
     */
    private void initLinphoneCoreValues(String basePath)
    {
        mLinphoneCore.setContext(mContext);
        mLinphoneCore.setRemoteRingbackTone(basePath + "/oldphone_mono.wav");
        mLinphoneCore.setTone(ToneID.CallWaiting, basePath + "/oldphone_mono.wav");
        mLinphoneCore.setRing(basePath + "/oldphone_mono.wav");
//        mLinphoneCore.setRing(null);
        mLinphoneCore.setRootCA(basePath + "/rootca.pem");
        mLinphoneCore.setPlayFile(basePath + "/toy_mono.wav");
        mLinphoneCore.setChatDatabasePath(basePath + "/linphone-history.db");

        int availableCores = Runtime.getRuntime().availableProcessors();
        mLinphoneCore.setCpuCount(availableCores);
        PayloadType h264 = null;
        for (PayloadType pt : mLinphoneCore.getVideoCodecs())
        {
            if (pt.getMime().equals("VP8")) h264 = pt;
        }

        if (h264 != null)
        {
            try
            {
                mLinphoneCore.enablePayloadType(h264, true);
            } catch (LinphoneCoreException e)
            {
                e.printStackTrace();
            }
        }
        Logger.e("-----isPayloadTypeEnabled------" + mLinphoneCore.isPayloadTypeEnabled(h264));
        mLinphoneCore.setPreferredFramerate(15);
        setBandwidthLimit(380);
    }

    public void setBandwidthLimit(int bandwidth)
    {
        mLinphoneCore.setUploadBandwidth(bandwidth);
        mLinphoneCore.setDownloadBandwidth(bandwidth);
    }

    /**
     * 设置用户代理
     */
    private void setUserAgent()
    {
        try
        {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            if (versionName == null)
            {
                versionName = String.valueOf(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode);
            }
            mLinphoneCore.setUserAgent("ZTMaintenance", versionName);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 设置默认相机
     */
    private void setFrontCamAsDefault()
    {
        int camId = 0;
        AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
        for (AndroidCamera androidCamera : cameras)
        {
            if (androidCamera.frontFacing)
                camId = androidCamera.id;
        }
        mLinphoneCore.setVideoDevice(camId);
    }

    /**
     * 开启轮询
     */
    private  TimerTask lTask;
    private void startIterate()
    {
        lTask = new TimerTask()
        {
            @Override
            public void run()
            {
                if(mLinphoneCore!=null) {
                    try{
                        mLinphoneCore.iterate();
                    }catch (Exception e){
                        Logger.t(TAG).d("e="+e.getMessage());
                    }

                }
            }
        };

        /*use schedule instead of scheduleAtFixedRate to avoid iterate from being call in burst after cpu wake up*/
        mTimer = new Timer("Linphone scheduler");
        mTimer.schedule(lTask, 0, 20);
    }

    @Override
    public void configuringStatus(LinphoneCore linphoneCore, LinphoneCore.RemoteProvisioningState remoteProvisioningState, String s)
    {

    }

    @Override
    public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage)
    {

    }

//    @Override
//    public void messageReceivedUnableToDecrypted(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage)
//    {
//
//    }

    @Override
    public void authInfoRequested(LinphoneCore linphoneCore, String s, String s1, String s2)
    {

    }

    @Override
    public void authenticationRequested(LinphoneCore linphoneCore, LinphoneAuthInfo linphoneAuthInfo, LinphoneCore.AuthMethod authMethod)
    {

    }

    @Override
    public void callStatsUpdated(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCallStats linphoneCallStats)
    {

    }

    @Override
    public void newSubscriptionRequest(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend, String s)
    {

    }

    @Override
    public void notifyPresenceReceived(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend)
    {

    }

    @Override
    public void dtmfReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, int i)
    {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneAddress linphoneAddress, byte[] bytes)
    {

    }

    @Override
    public void transferState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state)
    {

    }

    @Override
    public void infoReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneInfoMessage linphoneInfoMessage)
    {

    }

    @Override
    public void subscriptionStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, SubscriptionState subscriptionState)
    {

    }

    @Override
    public void publishStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, PublishState publishState)
    {

    }

    @Override
    public void show(LinphoneCore linphoneCore)
    {

    }

    @Override
    public void displayStatus(LinphoneCore linphoneCore, String s)
    {

    }

    @Override
    public void displayMessage(LinphoneCore linphoneCore, String s)
    {

    }

    @Override
    public void displayWarning(LinphoneCore linphoneCore, String s)
    {

    }

    @Override
    public void fileTransferProgressIndication(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, int i)
    {

    }

    @Override
    public void fileTransferRecv(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, byte[] bytes, int i)
    {

    }

    @Override
    public int fileTransferSend(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, ByteBuffer byteBuffer, int i)
    {
        return 0;
    }


    @Override
    public void callEncryptionChanged(LinphoneCore linphoneCore, LinphoneCall linphoneCall, boolean b, String s)
    {

    }

    @Override
    public void notifyReceived(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, String s, LinphoneContent linphoneContent)
    {

    }

    @Override
    public void isComposingReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom)
    {

    }

    @Override
    public void ecCalibrationStatus(LinphoneCore linphoneCore, LinphoneCore.EcCalibratorStatus ecCalibratorStatus, int i, Object o)
    {

    }

    @Override
    public void uploadProgressIndication(LinphoneCore linphoneCore, int i, int i1)
    {

    }

    @Override
    public void uploadStateChanged(LinphoneCore linphoneCore, LinphoneCore.LogCollectionUploadState logCollectionUploadState, String s)
    {

    }

    @Override
    public void friendListCreated(LinphoneCore linphoneCore, LinphoneFriendList linphoneFriendList)
    {

    }

    @Override
    public void friendListRemoved(LinphoneCore linphoneCore, LinphoneFriendList linphoneFriendList)
    {

    }

//    @Override
//    public void networkReachableChanged(LinphoneCore linphoneCore, boolean b)
//    {
//
//    }

}
