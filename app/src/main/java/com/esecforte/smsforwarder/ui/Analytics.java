package com.esecforte.smsforwarder.ui;

import com.esecforte.smsforwarder.MyApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    public static void track(String event) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MyApp.getInstance());
        firebaseAnalytics.logEvent(event, null);
    }

}
