package com.app.model;

/**
 * Created by admin on 9/8/2016.
 */
public class EventData {

    private String event_super_cate_name;
    private String event_cate_name;


    public EventData(){}

    public EventData(String event_super_cate_name, String event_cate_name){
        this.event_super_cate_name = event_super_cate_name;
        this.event_cate_name = event_cate_name;

    }

    public String getevent_super_cate_name(){
        return getevent_super_cate_name();
    }

    public String getevent_cate_name() {
        return event_cate_name;
    }
}
