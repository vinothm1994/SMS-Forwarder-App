package com.esecforte.smsforwarder.model;

import java.util.List;

// TODO: 13/2/19 move room
public class SMSForwardEntry {

    private int id;
    private boolean isEnabled;
    private String groupName;
    private List<String> smsNumbers;
    private List<String> forwardNumbers;



    public SMSForwardEntry(int id, boolean isEnabled, String groupName, List<String> smsNumbers, List<String> forwardNumbers) {
        this.id = id;
        this.isEnabled = isEnabled;
        this.groupName = groupName;
        this.smsNumbers = smsNumbers;
        this.forwardNumbers = forwardNumbers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getSmsNumbers() {
        return smsNumbers;
    }

    public void setSmsNumbers(List<String> smsNumbers) {
        this.smsNumbers = smsNumbers;
    }

    public List<String> getForwardNumbers() {
        return forwardNumbers;
    }

    public void setForwardNumbers(List<String> forwardNumbers) {
        this.forwardNumbers = forwardNumbers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
