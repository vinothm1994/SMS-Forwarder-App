package com.esecforte.smsforwarder.utils;

public interface AnalyticsEvents {
    String SMS_FORWARD_SUCCESS = "sms_forward_success";
    String SMS_FORWARD_FAILED = "sms_forward_failed";
    String SMS_FORWARD_ADDED = "sms_list_added";
    String SMS_FORWARD_DELETED = "sms_list_deleted";
    String SMS_FORWARD_ENABLED = "sms_list_enabled";
    String SMS_FORWARD_DISABLED = "sms_list_disabled";
    String AUTO_START_LAUNCHED = "auto_start_perm_launched";
}
