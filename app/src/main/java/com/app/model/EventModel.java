package com.app.model;

import java.util.ArrayList;

/**
 * Created by admin on 9/16/2016.
 */
public class EventModel {

    String event_super_category;
    String event_super_id;
    public ArrayList<EventDetail> alEvent;


    public String getEvent_super_id() {
        return event_super_id;
    }

    public void setEvent_super_id(String event_super_id) {
        this.event_super_id = event_super_id;
    }

    public void setEvent_super_category(String super_category){
        this.event_super_category = super_category;
    }

    public String getEvent_super_category(){
        return this.event_super_category;
    }

}
