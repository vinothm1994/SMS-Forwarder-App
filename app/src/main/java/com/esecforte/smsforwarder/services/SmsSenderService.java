package com.esecforte.smsforwarder.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import com.esecforte.smsforwarder.R;
import com.esecforte.smsforwarder.receivers.DeliveredSmsReceiver;
import com.esecforte.smsforwarder.receivers.SendSmsReceiver;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class SmsSenderService extends IntentService {


    public static final String TAG = SmsSenderService.class.getSimpleName();
    public static final String SMS_SENDER = "sender";
    public static final String SMS_BODY = "body";
    public static final String SMS_FORWARD_NOS = "nos";
    private static final String NOTIFICATION_CHANNEL_ID = "sms";


    public SmsSenderService() {
        super("SmsSenderService");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createdNotificationChennal() {
        String channelName = "Visitor Sync Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createdNotificationChennal();
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sending SMS")
                .setContentText("Sending SMS...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(101, mBuilder.build());
        if (intent != null) {
            sendSms(intent.getStringExtra(SMS_SENDER), intent.getStringExtra(SMS_BODY), intent.getStringArrayExtra(SMS_FORWARD_NOS));
        }
    }

    void sendSms(String from, String body, String[] toNums) {
        String messDesc = "from:" + from + "\n" + body;
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 10, new Intent(this, SendSmsReceiver.class), PendingIntent.FLAG_ONE_SHOT);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 11, new Intent(this, DeliveredSmsReceiver.class), PendingIntent.FLAG_ONE_SHOT);
        for (String toNo : toNums) {
            smsManager.sendTextMessage(toNo, null, messDesc, sentPendingIntent, deliveredPendingIntent);
        }

    }
}
