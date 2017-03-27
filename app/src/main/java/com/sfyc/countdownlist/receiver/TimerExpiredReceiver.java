package com.sfyc.countdownlist.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.sfyc.countdownlist.AlarmActivity;
import com.sfyc.countdownlist.R;

/**
 * Author :leilei on 2017/3/27 1443.
 */
public class TimerExpiredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        b.setSound(notification)
                .setContentTitle(context.getString(R.string.timer_finished))
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.timer_finished))
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentIntent(pIntent);

        Notification n = b.build();
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, n);
    }
}
