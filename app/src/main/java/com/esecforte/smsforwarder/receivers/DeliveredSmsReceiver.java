package com.esecforte.smsforwarder.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esecforte.smsforwarder.utils.AppUtils;

public class DeliveredSmsReceiver extends BroadcastReceiver {
    private static final String TAG = "DeliveredSmsReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String log = "no";
        String toNo=intent.getStringExtra("no");
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                log = "SMS delivered";
                AppUtils.appendLog("SMS delivered "+toNo, true);
                break;
            case Activity.RESULT_CANCELED:
                log = "SMS not delivered";
                AppUtils.appendLog("SMS not delivered "+toNo, true);
                break;
        }
        Log.i(TAG, "onReceive: " + log);
    }
}
