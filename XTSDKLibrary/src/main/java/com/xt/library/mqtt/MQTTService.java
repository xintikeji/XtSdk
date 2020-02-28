package com.xt.library.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.orhanobut.logger.Logger;
import com.xt.library.beans.MqMessageBean;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2018/12/29
 * @Description
 */

public class MQTTService extends Service implements org.fusesource.mqtt.client.Listener
{
    public static final String TAG = MQTTService.class.getSimpleName();

    public static final String HOST = "host";
    public static final String PORT = "port";

    private String host;
    private int port;


    private static MQTTService instance;

    private static MQTT mqtt;

    public CallbackConnection connection;
//    private IGetMessageCallBack IGetMessageCallBack;

    private MutableLiveData<MqMessageBean> messageLive = new MutableLiveData<>();

    public static String selfTopic = "";

    public static MQTTService instance()
    {
        if (instance == null)
        {
            synchronized (MQTTService.class)
            {
                if (instance == null) instance = new MQTTService();
            }
        }
        return instance;
    }

    public MutableLiveData<MqMessageBean> getMessageLive()
    {
        return messageLive;
    }


    public static boolean isReady()
    {
        return instance() != null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Logger.t(TAG).d("onCreate");
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
//            if (intent != null)
            {
                host = intent.getStringExtra(HOST);
                port = Integer.parseInt(intent.getStringExtra(PORT));
                init(host, port);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);

    }

    public void reInit(String host, int port)
    {
        init(host, port);
    }

    private void init(String host, int port)
    {
        try
        {
            mqtt = new MQTT();
            mqtt.setHost(host, port);
            mqtt.setCleanSession(true);
            mqtt.setReconnectAttemptsMax(6);
//            //设置重连的间隔时间
            mqtt.setReconnectDelay(2000);
            //设置心跳时间
            mqtt.setKeepAlive((short) 30);
            //设置缓冲的大小
//            mqtt.setSendBufferSize(2 * 1024 * 1024);
            connection = mqtt.callbackConnection();
            connection.listener(this);
            connection.connect(new Callback<Void>()
            {
                @Override
                public void onSuccess(Void value)
                {
                    Logger.t(TAG).d("MQTT连接成功:" + value);
//                    subscribeTopic(myTopic);
                }

                @Override
                public void onFailure(Throwable value)
                {
                    Logger.t(TAG).d("MQTT连接失败：" + value.getMessage());
                }
            });

        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void publish(final String topic, final String msg)
    {
        if (connection != null)
        {
            connection.getDispatchQueue().execute(new Runnable()
            {
                public void run()
                {
                    connection.publish(topic, msg.getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>()
                    {
                        @Override
                        public void onSuccess(Void value)
                        {
                            Logger.t(TAG).d("发送消息成功：" + topic);
                        }

                        @Override
                        public void onFailure(Throwable value)
                        {
                            Logger.t(TAG).d("发送消息失败：" + value.getMessage());
                        }
                    });
                }
            });
        }
    }

    /**
     * 单一订阅主题
     *
     * @param topic 主题名称
     */
    public void subscribeTopic(String topic)
    {
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        subscribeTopic(topics);
    }

    /**
     * 多主题订阅
     *
     * @param topics 主题名称集合
     */
    public void subscribeTopic(List<String> topics)
    {

//        List<Topic[]> result = new ArrayList<>();
        try
        {
            final Topic[] result = new Topic[topics.size()];
            for (int i = 0; i < topics.size(); i++)
            {

                result[i] = new Topic(topics.get(i), QoS.AT_LEAST_ONCE);
            }
            if (connection == null)
            {
//                ToastUtils.showShort("订阅失败...");
                return;
            }
            connection.subscribe(result, new Callback<byte[]>()
            {
                @Override
                public void onSuccess(byte[] value)
                {
                    Logger.t(TAG).d("订阅成功：" + Arrays.toString(value) + ",topics:" + result[0]);
                }

                @Override
                public void onFailure(Throwable value)
                {
                    Logger.t(TAG).d("订阅失败：" + value.getMessage());
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 单一解除订阅
     *
     * @param topic 主题名称
     */
    public void unSubscribeTopic(String topic)
    {
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        unSubscribeTopic(topics);
    }

    /**
     * 多订阅解除
     *
     * @param topics 主题集合
     */
    public void unSubscribeTopic(List<String> topics)
    {

//        List<Topic[]> result = new ArrayList<>();
        UTF8Buffer[] result = new UTF8Buffer[topics.size()];
        for (int i = 0; i < topics.size(); i++)
        {
            result[i] = new UTF8Buffer(topics.get(i));
        }
        connection.unsubscribe(result, new Callback<Void>()
        {
            @Override
            public void onSuccess(Void value)
            {
                Logger.t(TAG).d("解除订阅成功：" + value);
            }

            @Override
            public void onFailure(Throwable value)
            {
                Logger.t(TAG).d("解除订阅失败：" + value.getMessage());
            }
        });
    }

    @Override
    public void onDestroy()
    {
        Logger.t(TAG).d("onDestroy：");
//        instance=null;
//        if (connection != null)
//        {
//            connection.disconnect(new Callback<Void>()
//            {
//                @Override
//                public void onSuccess(Void value)
//                {
//                    Logger.t(TAG).d("断开连接成功：" + value);
//                }
//
//                @Override
//                public void onFailure(Throwable value)
//                {
//                    Logger.t(TAG).d("断开连接失败：" + value.getMessage());
//                }
//            });
//            connection=null;
//        }
//        stopSelf();
        super.onDestroy();
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNormal()
    {
//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

//        if (NetworkUtils.isWifiAvailable())
//        {
//            String name = info.getTypeName();
//            Logger.t(TAG).d("MQTT当前网络名称：" + name);
//            return true;
//        }
//        else
//        {
//            Logger.t(TAG).d("MQTT 没有可用网络");
        return false;
//        }
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        Logger.t(TAG).d("onBind");
        return new CustomBinder();
    }

//    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack)
//    {
//        this.IGetMessageCallBack = IGetMessageCallBack;
//    }

    @Override
    public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack)
    {
        ack.run();
        String mBody = new String(body.toByteArray());
        String mTopic = new String(topic.toByteArray());
        Logger.t(TAG).d("接收到消息 body:" + mBody + " | topic:" + mTopic + " | toString:" + body.toString());
        MqMessageBean bean = new MqMessageBean();
        bean.setTopic(mTopic);
        bean.setBody(mBody);
        messageLive.postValue(bean);

//        MqttMsgParser.onMsgReceive(this, mBody, mTopic);
//        if (IGetMessageCallBack != null)
//            IGetMessageCallBack.onMessageReceiver(mBody, mTopic);
    }

    @Override
    public void onConnected()
    {
        Logger.t(TAG).d("onConnected()");
    }

    @Override
    public void onDisconnected()
    {
        Logger.t(TAG).d("onDisconnected()");
//        connection.connect(new Callback<Void>()
//        {
//            @Override
//            public void onSuccess(Void value)
//            {
//                Logger.t(TAG).d("MQTT连接成功:" + value);
////                    subscribeTopic(myTopic);
//            }
//
//            @Override
//            public void onFailure(Throwable value)
//            {
//                Logger.t(TAG).d("MQTT连接失败：" + value.getMessage());
//            }
//        });
    }

    @Override
    public void onFailure(Throwable value)
    {
        Logger.t(TAG).d("onFailure():" + value.getMessage());
    }

    public void disConnect()
    {
        try
        {
            if (connection != null)
            {
                connection.disconnect(new Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void value)
                    {
                        Logger.t(TAG).d("断开连接成功：" + value);
                    }

                    @Override
                    public void onFailure(Throwable value)
                    {
                        Logger.t(TAG).d("断开连接失败：" + value.getMessage());
                    }
                });
                connection = null;
            }
            stopSelf();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class CustomBinder extends Binder
    {
        public MQTTService getService()
        {
            return MQTTService.this;
        }
    }

    public void toCreateNotification(String message)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MQTTService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);//3、创建一个通知，属性太多，使用构造器模式

        Notification notification = builder
                .setTicker("测试标题")
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText(message)
                .setContentInfo("")
                .setContentIntent(pendingIntent)//点击后才触发的意图，“挂起的”意图
                .setAutoCancel(true)        //设置点击之后notification消失
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(0, notification);
        notificationManager.notify(0, notification);

    }
}
