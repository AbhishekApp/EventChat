package com.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by admin on 5/1/2017.
 */

public class WonData {

String desc, burned, timeStamp, earned;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBurned() {
        return burned;
    }

    public void setBurned(String burned) {
        this.burned = burned;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEarned() {
        return earned;
    }

    public void setEarned(String earned) {
        this.earned = earned;
    }
}
