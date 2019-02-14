package com.esecforte.smsforwarder.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.esecforte.smsforwarder.data.AppPref;
import com.esecforte.smsforwarder.ui.Analytics;
import com.esecforte.smsforwarder.utils.AnalyticsEvents;

public class SendSmsReceiver extends BroadcastReceiver {

    private String TAG = "SendSmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppPref appPref = AppPref.getInstance(context);
        String log = "no";
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                log = "SMS sent successfully";
                appPref.getSuccessSmsCount(true);
                Analytics.track(AnalyticsEvents.SMS_FORWARD_SUCCESS);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                log = "Generic failure cause";
                Analytics.track(AnalyticsEvents.SMS_FORWARD_FAILED);
                appPref.getFailedSmsCount(true);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                appPref.getFailedSmsCount(true);
                Analytics.track(AnalyticsEvents.SMS_FORWARD_FAILED);
                log = "Service is currently unavailable";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Analytics.track(AnalyticsEvents.SMS_FORWARD_FAILED);
                appPref.getFailedSmsCount(true);
                log = "No pdu provided";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Analytics.track(AnalyticsEvents.SMS_FORWARD_FAILED);
                appPref.getFailedSmsCount(true);
                log = "Radio was explicitly turned off";
                break;
        }

        Log.i(TAG, "onReceive: " + log);
    }
}
