package com.mredrock.cyxbs.summer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.mredrock.cyxbs.summer.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    public NotificationUtils(Context context){
        super(context);
    }

    public void createNotificationChannel(){
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().createNotificationChannel(channel);
        }
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content){
            return new Notification.Builder(getApplicationContext(), id)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setAutoCancel(true);

    }
    public NotificationCompat.Builder getNotification_25(String title, String content){
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }
    public void sendNotification(String title, String content){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (title, content).setSmallIcon(R.drawable.summer_logo).build();
            getManager().notify(1,notification);
        }else{
            Notification notification = getNotification_25(title, content).setSmallIcon(R.drawable.summer_logo).build();
            getManager().notify(1,notification);
        }
    }
}
