package com.esecforte.smsforwarder.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.esecforte.smsforwarder.MyApp;
import com.esecforte.smsforwarder.R;
import com.esecforte.smsforwarder.utils.AppUtils;
import com.google.android.gms.common.util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.esecforte.smsforwarder.receivers.SmsReceiver.startSplit;

public class LogActivity extends AppCompatActivity {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);

    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle("Log");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView logTv = findViewById(R.id.logTv);
        try {
            filter();
        } catch (Exception e) {
            e.printStackTrace();

        }

        Collections.sort(pairList, new Comparator<Pair<Date, String>>() {
            @Override
            public int compare(Pair<Date, String> o1, Pair<Date, String> o2) {
                return o2.first.compareTo(o1.first);
            }
        });


        Iterator<Pair<Date, String>> it = pairList.iterator();
        final StringBuilder sb = new StringBuilder();
        String text = "";
            while (it.hasNext()) {
                sb.append(startSplit);
                sb.append("\n");
                sb.append(it.next().second.trim());
                sb.append("\n");

            }


        text = sb.toString();
         text = text.replaceAll("(?m)^[ \t]*\r?\n", "");
        if (!text.isEmpty())
        logTv.setText(text.trim());

    }

    List<Pair<Date,String>> pairList=new ArrayList<>();
    void filter() throws IOException {

        File logFile = new File(MyApp.getInstance().getFilesDir(), "log.txt");
        BufferedReader br = new BufferedReader(new FileReader(logFile));
        String line;
        StringBuilder text = new StringBuilder();
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append("\n");
        }
        String text1 = text.toString();
        StringBuffer stringBuffer = new StringBuffer();
        String[] arrays = text1.split(Pattern.quote(startSplit));
        for (String str : arrays) {
            String result = substringBetween(str, "[", "]");
            if (result != null) {
                try {
                    Date logdate = simpleDateFormat.parse(result);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -7);
                    if (logdate.after(calendar.getTime())) {
                        stringBuffer.append(startSplit);
                        stringBuffer.append(str);
                        pairList.add(new Pair<>(logdate, str));

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        logFile.delete();
        String s = stringBuffer.toString();
        s = s.replaceAll("(?m)^[ \t]*\r?\n", "");
        AppUtils.appendLog(s, false);
    }


}
