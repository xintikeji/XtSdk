package com.xt.library.interfaces.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.xt.library.api.RequestHeader;
import com.xt.library.beans.ElevatorStatusInfo;
import com.xt.library.beans.MqMessageBean;
import com.xt.library.beans.MqttCommandBean;
import com.xt.library.beans.MqttElevStatusBean;
import com.xt.library.beans.MyInfoBean;
import com.xt.library.interfaces.XtApiInterface;
import com.xt.library.interfaces.XtSdkInterface;
import com.xt.library.mqtt.MQTTService;
import com.xt.library.repositories.RequestRepository;
import com.xt.library.utils.CommonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zt.httplibrary.ApiException;
import com.zt.httplibrary.HttpUtils;
import com.zt.httplibrary.Subscriber.SilenceSubscriber;
import com.zt.linphonelibrary.LinphoneService;
import com.zt.linphonelibrary.LinphoneUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public final class XtSdkInterfaceImpl implements XtSdkInterface {

    private RequestRepository requestRepository = new RequestRepository();
    private String url = "http://xinti.dtznjg.com/";
    private String TAG = "XtSdkInterfaceImpl";
    private String toCallSipId;
    private XtApiInterface.OnVideoIntercomEvents onVideoEvents;
    private Context context;
    private String maintPhone = "";
    public static final String screenTopicHeader = "SiteWhere/zhongti/command/";
    public static String selfTopicHeader = "zhongti/weibao/";


    /**
     * @param application
     * @param token
     */
    @Override
    public void init(Context application, String token, XtApiInterface.OnInfoEvents onEvents) {
        this.context = application;
        //初始化校验
        RequestHeader.appToken = token;
        configOkHttp(true);
        HttpUtils.Companion.getInstance().initAllUrl(url);
        getMyInfo(context, onEvents);
    }

    /**
     * 获取个人信息
     */
    public void getMyInfo(final Context context, final XtApiInterface.OnInfoEvents onEvents) {
        this.context = context;
        if (!LinphoneService.isReady()) {
            try {
                LinphoneUtils.startService(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        requestRepository.getMyInfo(new SilenceSubscriber<MyInfoBean>() {
            @Override
            public void onNext(MyInfoBean response) {
                super.onNext(response);
                String sipId = response.getSip_id();
                String sipPassword = response.getSip_password();
                maintPhone = response.getMaint_staff_phonenum();
                LinphoneUtils.login(sipId, sipPassword, CommonUtils.sipServer, CommonUtils.sipServerPort);
                onEvents.requestResult("初始化成功！");

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                onEvents.requestResult("初始化失败！");
            }

            @Override
            public void onHandledError(ApiException apiE) {
                super.onHandledError(apiE);
                onEvents.requestResult("初始化失败！" + apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable) {
                super.onHandledNetError(throwable);
                onEvents.requestResult("初始化失败！");
            }
        });
        Intent intent = new Intent(context, MQTTService.class);
        intent.putExtra(MQTTService.HOST, CommonUtils.mqttServer);
        intent.putExtra(MQTTService.PORT, CommonUtils.mqttServerPort);
        context.startService(intent);
    }

    /**
     * 视频监控
     */
    private Timer timer;

    @Override
    public void getVideoMonitoring(String elevEquipmentCode, final XtApiInterface.OnVideoMonitoringEvents onEvents) {

        requestRepository.getSipAndCode(elevEquipmentCode, new SilenceSubscriber<String>() {
            @Override
            public void onNext(String response) {
                super.onNext(response);
                Logger.t(TAG).d("获取电梯信息：response=" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    toCallSipId = object.optString("sip_id");
                    String screenCode = object.optString("screen_code");
                    MqttCommandBean mqttCommandBean = new MqttCommandBean();
                    MqttCommandBean.CommandBeanX commandBeanX = new MqttCommandBean.CommandBeanX();
                    MqttCommandBean.NestingContextBean nestingContextBean = new MqttCommandBean.NestingContextBean();
                    MqttCommandBean.AssignmentBean assignmentBean = new MqttCommandBean.AssignmentBean();

                    MqttCommandBean.CommandBeanX.CommandBean commandBean = new MqttCommandBean.CommandBeanX.CommandBean();
                    commandBean.setNamespace("sip_rtsp");
                    commandBean.setName("sip_rtsp");
                    commandBean.setDescription("通话及推流");

                    MqttCommandBean.CommandBeanX.InvocationBean invocationBean = new MqttCommandBean.CommandBeanX.InvocationBean();
                    MqttCommandBean.CommandBeanX.InvocationBean.ParameterValuesBean parameterValuesBean = new MqttCommandBean.CommandBeanX.InvocationBean.ParameterValuesBean();
                    parameterValuesBean.setSip("0");
                    invocationBean.setParameterValues(parameterValuesBean);
                    commandBeanX.setInvocation(invocationBean);
                    commandBeanX.setCommand(commandBean);
                    mqttCommandBean.setCommand(commandBeanX);
                    mqttCommandBean.setNestingContext(nestingContextBean);
                    mqttCommandBean.setAssignment(assignmentBean);
                    Logger.t(TAG).d("发送mqtt消息！:" + new Gson().toJson(mqttCommandBean));
                    MQTTService.instance().publish(screenTopicHeader + screenCode, new Gson().toJson(mqttCommandBean));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestRepository.getTimestamp(toCallSipId, new SilenceSubscriber<String>() {

                                @Override
                                public void onNext(String response) {
                                    super.onNext(response);
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        String dateAddress = object.optString("dateAddress");
                                        String vlcPlayUrl = String.format("rtsp://zt.dtznjg.com:1554/%s/%s", toCallSipId, dateAddress);
                                        onEvents.videoMonitoringResult(vlcPlayUrl, "");
                                        timer = new Timer();
                                        TimerTask task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                requestRepository.uploadLookTimestamp(toCallSipId,
                                                        new SilenceSubscriber<String>() {

                                                        });
                                            }
                                        };
                                        timer.schedule(task, 1500, 1000 * 30 * 1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }, 4600);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 视频监控销毁
     */
    @Override
    public void videoEnd() {
        if (timer != null) {
            timer.cancel();
        }


    }

    /**
     * 获取电梯运行状态
     *
     * @param elevEquipmentCode
     * @param onVideoEvents
     */
    @Override
    public void getElevatorStatus(String elevEquipmentCode, final XtApiInterface.OnElevatorStatusEvents onVideoEvents) {
        requestRepository.getSipAndCode(elevEquipmentCode, new SilenceSubscriber<String>() {
            @Override
            public void onNext(String response) {
                super.onNext(response);
                Logger.t(TAG).d("获取电梯信息：response=" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    String lowerCode = object.optString("lower_code");
                    MQTTService.instance().subscribeTopic(selfTopicHeader + maintPhone);

                    String topic = screenTopicHeader + lowerCode;
                    String selfTopic = selfTopicHeader + maintPhone;
                    MqttElevStatusBean bean = new MqttElevStatusBean();
                    MqttElevStatusBean.CommandBeanX commandBeanX = new MqttElevStatusBean.CommandBeanX();
                    MqttElevStatusBean.CommandBeanX.CommandBean commandBean = new MqttElevStatusBean.CommandBeanX.CommandBean();
                    commandBean.setNamespace("currentMeasurement");
                    commandBean.setName("currentMeasurement");
                    commandBean.setDescription("电梯当前测量值上报");

                    List<MqttElevStatusBean.CommandBeanX.CommandBean.ParametersBean> parameters = new ArrayList<>();
                    MqttElevStatusBean.CommandBeanX.CommandBean.ParametersBean parametersBean = new MqttElevStatusBean.CommandBeanX.CommandBean.ParametersBean();
                    parametersBean.setName("topic");
                    parametersBean.setRequired(false);
                    parametersBean.setType("String");
                    parameters.add(parametersBean);
                    commandBean.setParameters(parameters);

                    commandBeanX.setCommand(commandBean);
                    MqttElevStatusBean.CommandBeanX.InvocationBean invocation = new MqttElevStatusBean.CommandBeanX.InvocationBean();
                    MqttElevStatusBean.CommandBeanX.InvocationBean.ParameterValuesBean parameterValues = new MqttElevStatusBean.CommandBeanX.InvocationBean.ParameterValuesBean();
                    parameterValues.setTopic(selfTopic);
                    invocation.setParameterValues(parameterValues);
                    commandBeanX.setInvocation(invocation);

                    MqttElevStatusBean.CommandBeanX.ParametersBeanX parameterss = new MqttElevStatusBean.CommandBeanX.ParametersBeanX();
                    parameterss.setTopic(selfTopic);
                    commandBeanX.setParameters(parameterss);
                    bean.setCommand(commandBeanX);
                    MqttElevStatusBean.NestingContextBean nestingContext = new MqttElevStatusBean.NestingContextBean();
                    MqttElevStatusBean.NestingContextBean.GatewayBean gateway = new MqttElevStatusBean.NestingContextBean.GatewayBean();
                    gateway.setToken(lowerCode);
                    nestingContext.setGateway(gateway);
                    bean.setNestingContext(nestingContext);
//                    String  msg = String.format(this.getString(R.string.mq_elevator_info_msg), selfTopic,
//                            selfTopic, lowerCode);
                    String msg = new Gson().toJson(bean);
                    Logger.t(TAG).d("topic：$topic | msg：$msg");
                    MQTTService.instance().publish(topic, msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        MQTTService.instance().getMessageLive().observeForever(new Observer<MqMessageBean>() {
            @Override
            public void onChanged(MqMessageBean mqMessageBean) {
//                Logger.t(TAG).d("维保收到消息：" + mqMessageBean.toString());
                onVideoEvents.elevatorStatusResult(JSON.parseObject(mqMessageBean.getBody(), ElevatorStatusInfo.class));
            }
        });

    }


    /**
     * 视频对讲
     */
    @Override
    public void getVideoIntercom(Context context, String elevEquipmentCode, XtApiInterface.OnVideoIntercomEvents onVideoEvents) {
        this.context = context;
        this.onVideoEvents = onVideoEvents;
        LinphoneCore lc = LinphoneUtils.getLcIfServiceNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(listenerBase);
        }
        requestRepository.getSipAndCode(elevEquipmentCode, new SilenceSubscriber<String>() {
            @Override
            public void onNext(String response) {
                super.onNext(response);
                Logger.t(TAG).d("获取电梯信息：response=" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    toCallSipId = object.optString("sip_id");
                    LinphoneUtils.callTo(toCallSipId, CommonUtils.sipServer, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private LinphoneCall.State tempState;
    LinphoneCoreListenerBase listenerBase = new LinphoneCoreListenerBase() {
        @Override
        public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
            super.registrationState(lc, cfg, state, smessage);
            Logger.t(TAG).d("registrationState():" + state + " | " + smessage);
            if (state == LinphoneCore.RegistrationState.RegistrationOk) {
                Logger.t(TAG).d("sip登陆成功--");
            } else if (state == LinphoneCore.RegistrationState.RegistrationFailed) {
                Logger.t(TAG).d("sip登录失败--");
            }
        }

        @Override
        public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
            super.callState(lc, call, state, message);
            Logger.t(TAG).d("siplinphone--" + state + ",message=" + message);
            if (state == LinphoneCall.State.Connected) {
                if (tempState == state) {
                    return;
                }
                tempState = state;

                LinphoneUtils.toggleSpeaker(true);
                LinphoneUtils.toggleMicro(false);
                disableMediaVoice(context);
                //获取时间戳
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestRepository.getTimestamp(toCallSipId, new SilenceSubscriber<String>() {

                                    @Override
                                    public void onNext(String response) {
                                        super.onNext(response);
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            String dateAddress = object.optString("dateAddress");
                                            String vlcPlayUrl = String.format("rtsp://zt.dtznjg.com:1554/%s/%s", toCallSipId, dateAddress);
                                            onVideoEvents.videoIntercomResult(vlcPlayUrl, "");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }, 4600);
                    }
                });


            } else if (state == LinphoneCall.State.CallEnd) {
                tempState = state;
            } else if (state == LinphoneCall.State.Error) {
                tempState = state;
                String errorString = "Bad Request Q.850;cause=41;text=\"NORMAL_TEMPORARY_FAILURE\"";
                String msg = "";
                if (message == (errorString)) {
                    msg = "对方通话中,只能查看视频监控！";
                } else {
                    msg = "通话异常,只能看视频监控！";
                }
                onVideoEvents.videoIntercomResult("", msg);
            } else if (state == LinphoneCall.State.StreamsRunning) {
                tempState = state;
            }

        }
    };


    /**
     * OkHttp初始化及配置
     */
    private OkHttpClient okHttpClient;

    private void configOkHttp(boolean isDebugModel) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        long readTimeout = 10000L;
        long conTimeout = 10000L;
        long writeTimeout = 10000L;
        if (isDebugModel) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            readTimeout = 10000L;
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(conTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//设置可访问的网站
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //LoggerInterceptor
    }

    private int currentVolume = 0;

    private void disableMediaVoice(Context context) {
        currentVolume = CommonUtils.getSystemVolume(context);
        CommonUtils.setSystemVolume(context, 0);
    }

    private void openMediaVoice(Context context) {
        CommonUtils.setSystemVolume(context, currentVolume);
    }
}
