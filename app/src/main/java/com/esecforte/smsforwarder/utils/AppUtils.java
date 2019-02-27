package com.esecforte.smsforwarder.utils;

import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.esecforte.smsforwarder.MyApp;
import com.esecforte.smsforwarder.model.SMSForwardEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppUtils {


    public static List<SMSForwardEntry> getFormattedSmsEntry(String str) {
        List<SMSForwardEntry> smsForwardEntries = new ArrayList<>();
        if (TextUtils.isEmpty(str))
            return new ArrayList<>();

        try {

            JSONArray jsonArray = new JSONArray(str);
            for (int i = 0; i < jsonArray.length(); i++) {
                List<String> smsNos = new ArrayList<>();
                List<String> phoneNos = new ArrayList<>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                boolean isEnabled = jsonObject.optBoolean("enable", false);
                String groupName = jsonObject.getString("group_name");
                JSONArray smsArray = jsonObject.getJSONArray("sms");
                JSONArray phoneNoArray = jsonObject.getJSONArray("phone");
                for (int j = 0; j < smsArray.length(); j++) {
                    String no = smsArray.getString(j);
                    smsNos.add(no);
                }
                for (int j = 0; j < phoneNoArray.length(); j++) {
                    String no = phoneNoArray.getString(j);
                    phoneNos.add(no);
                }
                SMSForwardEntry smsForwardEntry = new SMSForwardEntry(id, isEnabled, groupName, smsNos, phoneNos);
                smsForwardEntries.add(smsForwardEntry);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }


        return smsForwardEntries;
    }

    public static String getFormattedSmsEntry(List<SMSForwardEntry> smsForwardEntries) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < smsForwardEntries.size(); i++) {
            SMSForwardEntry entry = smsForwardEntries.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", entry.getId());
            jsonObject.put("enable", entry.isEnabled());
            jsonObject.put("group_name", entry.getGroupName());
            jsonObject.put("sms", new JSONArray(entry.getSmsNumbers()));
            jsonObject.put("phone", new JSONArray(entry.getForwardNumbers()));
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    public static void appendLog(String text, boolean addTime) {
        File logFile = new File(MyApp.getInstance().getFilesDir(), "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
            String date = simpleDateFormat.format(new Date());
            if (addTime)
              text = "["+date + "]" + text;

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
