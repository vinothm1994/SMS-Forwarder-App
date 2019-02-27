package com.esecforte.smsforwarder.ui;

import com.esecforte.smsforwarder.MyApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private static FirebaseAnalytics firebaseAnalytics;

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics = FirebaseAnalytics.getInstance(MyApp.getInstance());
    }

    public static void track(String event) {

        getFirebaseAnalytics().logEvent(event, null);
    }

}
