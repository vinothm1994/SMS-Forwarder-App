package com.esecforte.smsforwarder.utils;

public interface AnalyticsEvents {
    String SMS_FORWARD_SUCCESS = "sms-forward-success";
    String SMS_FORWARD_FAILED = "sms-forward-failed";
    String SMS_FORWARD_ADDED = "sms-list-added";
    String SMS_FORWARD_DELETED = "sms-list-deleted";
    String SMS_FORWARD_ENABLED = "sms-list-enabled";
    String SMS_FORWARD_DISABLED = "sms-list-disabled";
    String AUTO_START_LAUNCHED = "auto-start-perm-launched";
}
