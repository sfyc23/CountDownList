package com.sfyc.countdownlist.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sfyc.countdownlist.AlarmActivity;
import com.sfyc.countdownlist.R;

public class TimerExpiredReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "countdown_timer";
    private static final String CHANNEL_NAME = "CountDown Timer";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        Intent activityIntent = new Intent(context, AlarmActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
        );

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSound(notification)
                .setContentTitle(context.getString(R.string.timer_finished))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.timer_finished))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notificationInfo = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationInfo);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);
    }

    private int pendingIntentFlags(int baseFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return baseFlags | PendingIntent.FLAG_IMMUTABLE;
        }
        return baseFlags;
    }
}
