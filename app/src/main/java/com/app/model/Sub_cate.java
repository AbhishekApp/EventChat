package com.app.model;

import java.io.Serializable;

/**
 * Created by manish on 1/2/2017.
 */

public class Sub_cate implements Serializable {

    String event_id;
    String event_meta;
    String event_title;
    String event_time;
    String event_exp;
    String event_date;
    String event_image_url;
    String subscribed_user;

    public String getSubscribed_user() {
        return subscribed_user;
    }

    public void setSubscribed_user(String subscribed_user) {
        this.subscribed_user = subscribed_user;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_exp() {
        return event_exp;
    }

    public void setEvent_exp_time(String event_exp_time) {
        this.event_exp = event_exp_time;
    }
    //    String event_image_url;

    public String getEvent_meta() {
        return event_meta;
    }

    public void setEvent_meta(String event_meta) {
        this.event_meta = event_meta;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_image_url() {
        return event_image_url;
    }

    public void setEvent_image_url(String event_image_url) {
        this.event_image_url = event_image_url;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }






}
