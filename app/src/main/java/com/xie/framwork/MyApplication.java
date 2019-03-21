package com.xie.framwork;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.xie.framwork.activity.LoginActivity;
import com.xie.framwork.utils.Constants;

import es.dmoral.toasty.Toasty;

/**
 * Title:
 * Description:
 *
 * @author xie
 * @create 2019/3/1
 * @update 2019/3/1
 */
public class MyApplication extends Application {
    public static final String BROADCAST_LOGOUT = "broadcast_logout";
    private static MyApplication instance;
    private myreceiver recevier;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initToasty();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = Constants.NOTIFICATION_CHANNELID;
            String channelName = "任务消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        recevier = new myreceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_LOGOUT);
        //当网络发生变化的时候，系统广播会发出值为android.net.conn.CONNECTIVITY_CHANGE这样的一条广播
        registerReceiver(recevier, intentFilter);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setSound(null, null);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initToasty() {
        Toasty.Config.getInstance()
                .allowQueue(true) // optional (prevents several Toastys from queuing)
                .apply(); // required
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(recevier);
        super.onTerminate();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public class myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            intent = new Intent(MyApplication.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.this.startActivity(intent);
        }
    }
}
