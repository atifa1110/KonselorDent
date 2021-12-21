package com.dentist.konselorhalodent.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dentist.konselorhalodent.Activity.SignInActivity;
import com.dentist.konselorhalodent.Model.Constant;
import com.dentist.konselorhalodent.Model.Util;
import com.dentist.konselorhalodent.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class ChatMessagesService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "channel";
    String img_url = null;
    Bitmap image_bitmap = null;

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Util.updateDeviceToken(this,s);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //GET DATA
        String title = remoteMessage.getData().get(Constant.NOTIFICATION_TITLE);
        String message = remoteMessage.getData().get(Constant.NOTIFICATION_MESSAGE);

        Intent intentChat = new Intent(this, SignInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentChat, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constant.CHANNEL_ID,
                    Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription(Constant.CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, Constant.CHANNEL_ID);
        } else {
            long[] vibrate = {0, 100, 200, 300};
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_dentalcare)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setVibrate(vibrate)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (message.startsWith("https://firebasestorage.")) {
                notificationBuilder.setContentText("Send Picture");
            } else {
                notificationBuilder.setContentText(message);
            }
            notificationManager.notify(999, notificationBuilder.build());
        }
    }
}
