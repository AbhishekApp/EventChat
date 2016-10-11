package com.app.model;

import java.io.Serializable;

/**
 * Created by admin on 9/16/2016.
 */
public class EventDetail implements Serializable{

    String super_category_name;
    String catergory_id;
    String category_name;
    String event_meta;
    String event_title;
    String event_image_url;
    long event_time;

    public void setSuper_category_name(String super_category_name) {
        this.super_category_name = super_category_name;
    }
    public void setCatergory_id(String catergory_id){
        this.catergory_id = catergory_id;
    }
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

    public void setEvent_time(long event_time){
        this.event_time = event_time;
    }

    public String getSuper_category_name() {
        return super_category_name;
    }

    public String getCatergory_id(){
        return this.catergory_id;
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

    public long getEvent_time(){
        return event_time;
    }

}
