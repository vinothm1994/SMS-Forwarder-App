package com.esecforte.smsforwarder.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeliveredSmsReceiver extends BroadcastReceiver {
    private static final String TAG = "DeliveredSmsReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String log = "no";
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                log = "SMS delivered";
                break;
            case Activity.RESULT_CANCELED:
                log = "SMS not delivered";
                break;
        }
        Log.i(TAG, "onReceive: " + log);
    }
}
