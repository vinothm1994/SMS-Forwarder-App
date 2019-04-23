package com.esecforte.smsforwarder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.esecforte.smsforwarder.data.AppPref;
import com.esecforte.smsforwarder.model.SMSForwardEntry;
import com.esecforte.smsforwarder.services.SmsSenderService;
import com.esecforte.smsforwarder.utils.AppUtils;

import java.util.HashSet;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {


    private static final String TAG = SmsReceiver.class.getSimpleName();

    
    public  static String startSplit="******************************";

    public void onReceive(Context context, Intent intent) {
         String sender = null;
         String message = null;
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null && pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                sender = messages[0].getOriginatingAddress();
                message = sb.toString();
            }

            if (!TextUtils.isEmpty(sender) && !TextUtils.isEmpty(message)) {
                checkAndSendSms(context, sender, message);

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }



    }

    public static void checkAndSendSms(Context context, String sender, String message) {
        AppPref appPref = AppPref.getInstance(context);
        List<SMSForwardEntry> datas = AppUtils.getFormattedSmsEntry(appPref.getString(AppPref.USER_PH_NO_DATA));
        HashSet<String> forWardNos = new HashSet<>();
        AppUtils.appendLog( startSplit, false);
        AppUtils.appendLog( "SMS received from:'"+sender+"'", true);
        for (SMSForwardEntry entry : datas) {
            if (!entry.isEnabled()){
                AppUtils.appendLog( " "+entry.getGroupName()+" not Enabled",false);
                break;
            }
            List<String> smsNos = entry.getSmsNumbers();

            AppUtils.appendLog("Checking Group "+entry.getGroupName(), false);
            for (String sms : smsNos) {

                if (sms.equalsIgnoreCase(sender)||sender.toLowerCase().contains(sms)) {
                    forWardNos.addAll(entry.getForwardNumbers());
                    AppUtils.appendLog("'"+sms + "' sms matched!!!! ", false);
                    break;
                } else
                    AppUtils.appendLog("'"+sms + "' sms not matched!!!! ", false);


                // for bank otp sms
                boolean isStop=false;

                String[] sendersplitNo=sender.split("-");
                sms=sms.toLowerCase();
                if (sendersplitNo.length>=2){
                    for (int i = 0, sendersplitNoLength = sendersplitNo.length; i < sendersplitNoLength; i++) {
                        String incomeSmsNo = sendersplitNo[i];
                        if (i==0&&incomeSmsNo.length()<=2){
                            break;//AT-SBIATM to skip when AT comes with sms
                        }
                        incomeSmsNo = incomeSmsNo.toLowerCase();
                        if (incomeSmsNo.contains(sms) || sms.contains(incomeSmsNo)) {
                            forWardNos.addAll(entry.getForwardNumbers());
                            AppUtils.appendLog("'" + sms + "' sms matched### " + incomeSmsNo, false);
                            isStop = true;
                            break;
                        } else
                            AppUtils.appendLog("'" + sms + "' sms not matched### " + incomeSmsNo, false);

                    }
                }

                if (isStop){
                    break;
                }


            }
        }


        if (!forWardNos.isEmpty()) {
            AppUtils.appendLog( "##############################", false);
            Intent intent1 = new Intent(context, SmsSenderService.class);
            intent1.putExtra(SmsSenderService.SMS_FORWARD_NOS, forWardNos.toArray(new String[0]));
            intent1.putExtra(SmsSenderService.SMS_SENDER, sender);
            intent1.putExtra(SmsSenderService.SMS_BODY, message);
            context.startService(intent1);
        }
    }


}