package com.app.model;

/**
 * Created by admin on 9/16/2016.
 */
public class EventDetail {

    String category_name;
    String event_meta;
    String event_title;
    String event_image_url;

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setEvent_meta(String event_meta) {
        this.event_meta = event_meta;
    }

    public void setEvent_image_url(String event_image_url) {
        this.event_image_url = event_image_url;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getEvent_image_url() {
        return event_image_url;
    }

    public String getEvent_meta() {
        return event_meta;
    }

    public String getEvent_title() {
        return event_title;
    }
}
