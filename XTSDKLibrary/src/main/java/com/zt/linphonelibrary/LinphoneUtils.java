package com.zt.linphonelibrary;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneNatPolicy;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Administrator
 * @Date 2018/12/10
 * @Version 1.0
 */
public class LinphoneUtils
{
    private static final String TAG = LinphoneUtils.class.getSimpleName();

    /**
     * 开启服务
     *
     * @param activity 上下文
     */
    public static void startService(Context activity)
    {//第一次返回false 执行下面代码
        //因为LinphoneService第一次并没有创建 所以第一次走下面的方法
        if (!LinphoneService.isReady()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);//标识Activity为一个程序的开始，我猜可能是跳转到最开始的activity
            intent.setClass(activity, LinphoneService.class);//没返回的跳转  这里可能有LinphoneService的创建
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
//            activity.startForegroundService(intent);
//        else
            activity.startService(intent);//启动服务  后台执行
        }

    }

    public static synchronized final LinphoneCore getLc()
    {
        return LinphoneService.getLc();
    }

    public static synchronized LinphoneCore getLcIfServiceNotDestroyedOrNull()
    {
        if (LinphoneService.instance() == null)
        {
            // Can occur if the UI thread play a posted event but in the meantime the LinphoneManager was destroyed
            // Ex: stop call and quickly terminate application.
            return null;
        }
        return getLc();
    }

    private static BandwidthManager bm()
    {
        return BandwidthManager.getInstance();
    }

    /**
     * 登陆sip
     *
     * @param sipUser  用户名 eg.1001
     * @param password 密码
     * @param port     端口
     */
    public static void login(String sipUser, String password, String host, String port)
    {
        try
        {
//            getLc().clearAuthInfos();
//            getLc().clearCallLogs();
//            getLc().clearProxyConfigs();


            String sipAddress = "sip:" + sipUser + "@" + host;
            LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(sipAddress);
            String username = address.getUserName();

            String domain = address.getDomain();

            LinphoneProxyConfig[] proxyConfigList = getLc().getProxyConfigList();
            for (LinphoneProxyConfig linphoneProxyConfig : proxyConfigList)
            {
                getLc().removeProxyConfig(linphoneProxyConfig);
            }//删除原来的
            getLc().addAuthInfo(LinphoneCoreFactory.instance().
                    createAuthInfo(username, password, null, domain + ":" + port));
            // create proxy config
            LinphoneProxyConfig proxyCfg = getLc().createProxyConfig(sipAddress, domain + ":" + port, null, true);
            proxyCfg.enablePublish(true);
            proxyCfg.setExpires(2000);
            getLc().addProxyConfig(proxyCfg); // add it to linphone

            getLc().setDefaultProxyConfig(proxyCfg);//注册一次就好了  下次启动就不用注册
        } catch (LinphoneCoreException e)
        {
            e.printStackTrace();
        }
    }
    //注销
    public static void logout() {
        LinphoneProxyConfig[] proxyConfigList = getLc().getProxyConfigList();
        for (LinphoneProxyConfig linphoneProxyConfig : proxyConfigList)
        {
            getLc().removeProxyConfig(linphoneProxyConfig);
        }
        LinphoneAuthInfo[] authInfoList = getLc().getAuthInfosList();
        for (LinphoneAuthInfo authInfo : authInfoList)
        {
            getLc().removeAuthInfo(authInfo);
        }
        getLc().refreshRegisters();
    }
    /**
     * 打电话
     *
     * @param username    用户
     * @param host        server
     * @param isVideoCall 是否为视频
     */
    public static void callTo(String username, String host, boolean isVideoCall)
    {
        try
        {
            LinphoneAddress address = getLc().interpretUrl(username + "@" + host);
//            address.setDisplayName(SharePreUtils.getUserPhone(null));
            LinphoneCallParams params = getLc().createCallParams(null);
            if (isVideoCall)
            {
                params.setVideoEnabled(true);
                params.enableLowBandwidth(false);
            }
            else
            {
                params.setVideoEnabled(false);
            }
            params.addCustomHeader("JILINPENG", "false111");
            LinphoneCall call = getLc().inviteAddressWithParams(address, params);

            if (call == null)
            {
                Logger.t(TAG).d("Could not place call to " + username);
                return;
            }
        } catch (LinphoneCoreException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendTextMsg(String msg, String toUser, String host)
    {
        LinphoneCore lc = getLcIfServiceNotDestroyedOrNull();
        LinphoneAddress lAddress = null;
        String sipUri = "sip:" + toUser + "@" + host;
        if (lc != null)
        {
            try
            {
                lAddress = lc.interpretUrl(sipUri);
            } catch (LinphoneCoreException e)
            {
                e.printStackTrace();
            }
            if (lAddress != null)
            {
                LinphoneChatRoom chatRoom = lc.getChatRoom(lAddress);
                chatRoom.markAsRead();
                LinphoneChatMessage message = chatRoom.createLinphoneChatMessage(msg);
                chatRoom.sendChatMessage(message);
            }
        }
    }


    /**
     * 接电话
     */
    public static void acceptCall()
    {

        try
        {
            /*设置初始话视频电话，设置了这个你拨号的时候就默认为使用视频发起通话了*/
            //instance.getLC().setVideoPolicy(true, instance.getLC().getVideoAutoAcceptPolicy());
            /*设置自动接听视频通话的请求，也就是说只要是视频通话来了，直接就接通，不用按键确定，这是我们的业务流，不用理会*/
            getLc().setVideoPolicy(getLc().getVideoAutoInitiatePolicy(), true);
            /*这是允许视频通话，这个选了false就彻底不能接听或者拨打视频电话了*/
            getLc().enableVideo(true, true);
            LinphoneCall currentCall = getLc().getCurrentCall();
            if (currentCall != null)
            {
                LinphoneCallParams params = getLc().createCallParams(currentCall);
                if (params != null)
                {
                    if (params.getVideoEnabled())
                    {
//                        ToastUtils.showShort("接电话开启视频");
                        params.setVideoEnabled(true);
                    }

                }
                getLc().acceptCallWithParams(currentCall, params);
            }
        } catch (LinphoneCoreException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 挂断电话
     */
    public static void hangUp()
    {
        LinphoneCall currentCall = getLc().getCurrentCall();
        if (currentCall != null)
        {
            getLc().terminateCall(currentCall);
        }
        else if (getLc().isInConference())
        {
            getLc().terminateConference();
        }
        else
        {
            getLc().terminateAllCalls();
        }
    }


    public static LinphoneNatPolicy getOrCreateNatPolicy()
    {
        LinphoneNatPolicy nat = LinphoneService.getLc().getNatPolicy();
        if (nat == null)
        {
            nat = LinphoneService.getLc().createNatPolicy();
        }
        return nat;
    }

    public static LinphoneProxyConfig getOrCreateProxyConfig()
    {
        LinphoneProxyConfig nat = LinphoneService.getLc().getDefaultProxyConfig();
        if (nat == null)
        {
            nat = LinphoneService.getLc().createProxyConfig();
        }
        return nat;
    }


    public static void copyIfNotExist(Context context, int ressourceId, String target) throws IOException
    {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists())
        {
            copyFromPackage(context, ressourceId, lFileToCopy.getName());
        }
    }

    public static void copyFromPackage(Context context, int ressourceId, String target) throws IOException
    {
        FileOutputStream lOutputStream = context.openFileOutput(target, 0);
        InputStream lInputStream = context.getResources().openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1)
        {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

    /**
     * 是否静音
     *
     * @param isMicMuted
     */
    public static void toggleMicro(boolean isMicMuted)
    {
        getLc().muteMic(isMicMuted);
    }

    /**
     * 是否外放
     *
     * @param isSpeakerEnabled
     */
    public static void toggleSpeaker(boolean isSpeakerEnabled)
    {
        getLc().enableSpeaker(isSpeakerEnabled);
    }


    public static void disableVideo(final boolean videoDisabled)
    {
//        ToastUtils.showShort("disableVideo():" + videoDisabled);
        final LinphoneCall call = getLc().getCurrentCall();
        if (call == null)
        {
            return;
        }

        if (videoDisabled)
        {
            LinphoneCallParams params = getLc().createCallParams(call);
            params.setVideoEnabled(false);
            getLc().updateCall(call, params);
        }
        else
        {
            if (!call.getRemoteParams().isLowBandwidthEnabled())
            {
                addVideo();
            }
            else
            {
                Logger.t(TAG).d("对方带宽过低，无法开启视频");
            }
        }
    }

    public static boolean isVideoEnabled(LinphoneCall call)
    {
        if (call != null)
        {
            return call.getCurrentParamsCopy().getVideoEnabled();
        }
        return false;
    }

    /**
     * @return false if already in video call.
     */
    public static boolean addVideo()
    {
        LinphoneCall call = getLc().getCurrentCall();
        enableCamera(call, true);
        return reinviteWithVideo();
    }

    private static void enableCamera(LinphoneCall call, boolean enable)
    {
        if (call != null)
        {
            call.enableCamera(enable);
        }
    }

    /**
     * Add video to a currently running voice only call.
     * No re-invite is sent if the current call is already video
     * or if the bandwidth settings are too low.
     *
     * @return if updateCall called
     */
    private static boolean reinviteWithVideo()
    {
        LinphoneCore lc = getLc();
        LinphoneCall lCall = lc.getCurrentCall();
        if (lCall == null)
        {
            Log.e("Trying to reinviteWithVideo while not in call: doing nothing");
            return false;
        }
        LinphoneCallParams params = lCall.getCurrentParamsCopy();

        if (params.getVideoEnabled()) return false;


        // Check if video possible regarding bandwidth limitations
        bm().updateWithProfileSettings(lc, params);

        // Abort if not enough bandwidth...
        if (!params.getVideoEnabled())
        {
            return false;
        }
//        ToastUtils.showShort("");
        // Not yet in video call: try to re-invite with video
        lc.updateCall(lCall, params);
        return true;
    }

    /**
     * 回声消除
     */
    public static void setEchoCancellation(boolean enable)
    {

        getLc().enableEchoCancellation(enable);

    }


}
